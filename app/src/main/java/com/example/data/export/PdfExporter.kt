package com.example.data.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.DocumentSection
import com.example.data.model.Project
import com.example.data.model.Requirement
import com.example.data.model.SrsDocument
import com.example.data.model.UseCase
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private const val PAGE_WIDTH = 595 // Standard A4 points (72 dpi)
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_LEFT = 50f
    private const val MARGIN_RIGHT = 545f
    private const val MARGIN_TOP = 60f
    private const val MARGIN_BOTTOM = 780f
    private const val CONTENT_WIDTH = MARGIN_RIGHT - MARGIN_LEFT

    fun generatePdf(
        context: Context,
        project: Project,
        document: SrsDocument,
        sections: List<DocumentSection>,
        requirements: List<Requirement>,
        useCases: List<UseCase>
    ): File {
        val pdfDocument = PdfDocument()
        var pageNumber = 1

        val titlePaint = Paint().apply {
            color = Color.rgb(11, 19, 43)
            textSize = 24f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val h1Paint = Paint().apply {
            color = Color.rgb(0, 102, 255) // Electric Blue
            textSize = 16f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val h2Paint = Paint().apply {
            color = Color.rgb(28, 37, 65)
            textSize = 13f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 9f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        val headerFillPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        // ================= PAGE 1: COVER PAGE =================
        val pageInfo1 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        val canvas1 = page1.canvas

        // Decorative banner accent at top
        val bannerPaint = Paint().apply {
            color = Color.rgb(0, 102, 255)
            style = Paint.Style.FILL
        }
        canvas1.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 12f, bannerPaint)

        // Navy top bar
        val navyTopPaint = Paint().apply {
            color = Color.rgb(11, 19, 43)
            style = Paint.Style.FILL
        }
        canvas1.drawRect(MARGIN_LEFT, 80f, MARGIN_RIGHT, 86f, bannerPaint)

        // Title
        val docTypePaint = Paint().apply {
            color = Color.rgb(0, 102, 255)
            textSize = 12f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas1.drawText("SOFTWARE REQUIREMENTS SPECIFICATION", MARGIN_LEFT, 120f, docTypePaint)
        canvas1.drawText("IEEE 830-1998 / ISO 29148 COMPLIANT", MARGIN_LEFT, 136f, metaPaint)

        // Project Title
        titlePaint.textSize = 26f
        canvas1.drawText(project.title, MARGIN_LEFT, 190f, titlePaint)

        // Subtitle / Document Title
        h1Paint.textSize = 15f
        canvas1.drawText(document.title, MARGIN_LEFT, 215f, h1Paint)

        // Project Key & Category
        val tagBoxPaint = Paint().apply {
            color = Color.rgb(224, 242, 254)
            style = Paint.Style.FILL
        }
        canvas1.drawRoundRect(MARGIN_LEFT, 235f, MARGIN_LEFT + 140f, 260f, 6f, 6f, tagBoxPaint)
        val tagTextPaint = Paint().apply {
            color = Color.rgb(3, 105, 161)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas1.drawText("KEY: ${project.keyPrefix} | ${project.category}", MARGIN_LEFT + 10f, 252f, tagTextPaint)

        // Description paragraph
        val descLines = wrapText(project.description, bodyPaint, CONTENT_WIDTH)
        var descY = 300f
        for (line in descLines.take(5)) {
            canvas1.drawText(line, MARGIN_LEFT, descY, bodyPaint)
            descY += 16f
        }

        // Metadata box
        canvas1.drawRoundRect(MARGIN_LEFT, 440f, MARGIN_RIGHT, 660f, 8f, 8f, headerFillPaint)
        canvas1.drawRoundRect(MARGIN_LEFT, 440f, MARGIN_RIGHT, 660f, 8f, 8f, linePaint)

        var metaY = 475f
        val metaFieldPaint = Paint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        val metaValPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val metadataEntries = listOf(
            "Document Version:" to document.version,
            "Document Status:" to document.status,
            "Author / Architect:" to document.author,
            "Organization:" to document.organization,
            "Target Audience:" to project.targetAudience,
            "Date of Publication:" to dateFormat.format(Date(document.lastModified)),
            "Classification:" to document.confidentiality
        )

        for ((field, value) in metadataEntries) {
            canvas1.drawText(field, MARGIN_LEFT + 20f, metaY, metaFieldPaint)
            canvas1.drawText(value, MARGIN_LEFT + 180f, metaY, metaValPaint)
            metaY += 25f
        }

        // Footer notice
        canvas1.drawText("SpecForge SRS Platform — Built by Evans Ayanlude Abayomi (Founder & CEO, WebForge Team)", MARGIN_LEFT, 770f, metaPaint)

        pdfDocument.finishPage(page1)
        pageNumber++

        // ================= PAGE 2+: SECTIONS & REQUIREMENTS =================
        var currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var currentPage = pdfDocument.startPage(currentPageInfo)
        var currentCanvas = currentPage.canvas
        var currentY = MARGIN_TOP

        fun drawHeaderAndFooter(canvas: android.graphics.Canvas, pNum: Int) {
            canvas.drawText("${project.title} — SRS v${document.version}", MARGIN_LEFT, 35f, metaPaint)
            canvas.drawLine(MARGIN_LEFT, 42f, MARGIN_RIGHT, 42f, linePaint)
            canvas.drawLine(MARGIN_LEFT, MARGIN_BOTTOM + 15f, MARGIN_RIGHT, MARGIN_BOTTOM + 15f, linePaint)
            canvas.drawText("SpecForge by Evans Ayanlude Abayomi • WebForge Team", MARGIN_LEFT, MARGIN_BOTTOM + 30f, metaPaint)
            canvas.drawText("Page $pNum", MARGIN_RIGHT - 40f, MARGIN_BOTTOM + 30f, metaPaint)
        }

        drawHeaderAndFooter(currentCanvas, pageNumber)

        // Draw Table of Contents
        currentCanvas.drawText("Table of Contents", MARGIN_LEFT, currentY + 10f, h1Paint)
        currentY += 30f

        for (section in sections.filter { it.isEnabled }) {
            if (currentY > MARGIN_BOTTOM - 40f) {
                pdfDocument.finishPage(currentPage)
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = pdfDocument.startPage(currentPageInfo)
                currentCanvas = currentPage.canvas
                currentY = MARGIN_TOP
                drawHeaderAndFooter(currentCanvas, pageNumber)
            }
            currentCanvas.drawText("${section.sectionNumber}  ${section.title}", MARGIN_LEFT + 10f, currentY, bodyPaint)
            currentCanvas.drawLine(MARGIN_LEFT + 250f, currentY - 2f, MARGIN_RIGHT - 30f, currentY - 2f, linePaint)
            currentCanvas.drawText("Section", MARGIN_RIGHT - 25f, currentY, metaPaint)
            currentY += 20f
        }

        currentCanvas.drawText("4.0  System Requirements Specification Matrix", MARGIN_LEFT + 10f, currentY, bodyPaint)
        currentCanvas.drawLine(MARGIN_LEFT + 290f, currentY - 2f, MARGIN_RIGHT - 30f, currentY - 2f, linePaint)
        currentCanvas.drawText("Table", MARGIN_RIGHT - 25f, currentY, metaPaint)
        currentY += 20f

        currentCanvas.drawText("5.0  Requirements Traceability & Use Cases", MARGIN_LEFT + 10f, currentY, bodyPaint)
        currentCanvas.drawLine(MARGIN_LEFT + 290f, currentY - 2f, MARGIN_RIGHT - 30f, currentY - 2f, linePaint)
        currentCanvas.drawText("Table", MARGIN_RIGHT - 25f, currentY, metaPaint)
        currentY += 35f

        // Draw Sections Content
        for (section in sections.filter { it.isEnabled }) {
            // Check page break
            if (currentY > MARGIN_BOTTOM - 60f) {
                pdfDocument.finishPage(currentPage)
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = pdfDocument.startPage(currentPageInfo)
                currentCanvas = currentPage.canvas
                currentY = MARGIN_TOP
                drawHeaderAndFooter(currentCanvas, pageNumber)
            }

            // Section Heading
            currentCanvas.drawText("${section.sectionNumber} ${section.title}", MARGIN_LEFT, currentY, h1Paint)
            currentY += 20f

            // Section Body Lines
            val lines = wrapText(section.content, bodyPaint, CONTENT_WIDTH)
            for (line in lines) {
                if (currentY > MARGIN_BOTTOM - 20f) {
                    pdfDocument.finishPage(currentPage)
                    pageNumber++
                    currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    currentPage = pdfDocument.startPage(currentPageInfo)
                    currentCanvas = currentPage.canvas
                    currentY = MARGIN_TOP
                    drawHeaderAndFooter(currentCanvas, pageNumber)
                }
                currentCanvas.drawText(line, MARGIN_LEFT, currentY, bodyPaint)
                currentY += 15f
            }
            currentY += 15f
        }

        // Draw Requirements Specification Table
        if (currentY > MARGIN_BOTTOM - 80f) {
            pdfDocument.finishPage(currentPage)
            pageNumber++
            currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            currentPage = pdfDocument.startPage(currentPageInfo)
            currentCanvas = currentPage.canvas
            currentY = MARGIN_TOP
            drawHeaderAndFooter(currentCanvas, pageNumber)
        }

        currentCanvas.drawText("4.0 System Requirements Specification", MARGIN_LEFT, currentY, h1Paint)
        currentY += 25f

        for (req in requirements) {
            if (currentY > MARGIN_BOTTOM - 90f) {
                pdfDocument.finishPage(currentPage)
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = pdfDocument.startPage(currentPageInfo)
                currentCanvas = currentPage.canvas
                currentY = MARGIN_TOP
                drawHeaderAndFooter(currentCanvas, pageNumber)
            }

            // Requirement Card Header
            currentCanvas.drawRoundRect(MARGIN_LEFT, currentY - 12f, MARGIN_RIGHT, currentY + 16f, 4f, 4f, headerFillPaint)
            currentCanvas.drawRoundRect(MARGIN_LEFT, currentY - 12f, MARGIN_RIGHT, currentY + 16f, 4f, 4f, linePaint)

            val reqIdPaint = Paint().apply {
                color = Color.rgb(0, 102, 255)
                textSize = 10f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                isAntiAlias = true
            }
            currentCanvas.drawText(req.reqId, MARGIN_LEFT + 8f, currentY + 4f, reqIdPaint)
            currentCanvas.drawText(req.title, MARGIN_LEFT + 95f, currentY + 4f, h2Paint)
            currentCanvas.drawText("[${req.type}] [${req.priority}]", MARGIN_RIGHT - 110f, currentY + 4f, metaPaint)
            currentY += 26f

            // Statement
            val stmtLines = wrapText("Statement: ${req.statement}", bodyPaint, CONTENT_WIDTH - 16f)
            for (line in stmtLines) {
                if (currentY > MARGIN_BOTTOM - 15f) {
                    pdfDocument.finishPage(currentPage)
                    pageNumber++
                    currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    currentPage = pdfDocument.startPage(currentPageInfo)
                    currentCanvas = currentPage.canvas
                    currentY = MARGIN_TOP
                    drawHeaderAndFooter(currentCanvas, pageNumber)
                }
                currentCanvas.drawText(line, MARGIN_LEFT + 8f, currentY, bodyPaint)
                currentY += 14f
            }

            // Acceptance Criteria
            if (req.acceptanceCriteria.isNotBlank()) {
                val acLines = wrapText("Acceptance: ${req.acceptanceCriteria}", metaPaint, CONTENT_WIDTH - 16f)
                for (line in acLines) {
                    if (currentY > MARGIN_BOTTOM - 15f) {
                        pdfDocument.finishPage(currentPage)
                        pageNumber++
                        currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                        currentPage = pdfDocument.startPage(currentPageInfo)
                        currentCanvas = currentPage.canvas
                        currentY = MARGIN_TOP
                        drawHeaderAndFooter(currentCanvas, pageNumber)
                    }
                    currentCanvas.drawText(line, MARGIN_LEFT + 8f, currentY, metaPaint)
                    currentY += 13f
                }
            }

            currentY += 12f
        }

        // Draw Traceability Matrix
        if (useCases.isNotEmpty()) {
            if (currentY > MARGIN_BOTTOM - 80f) {
                pdfDocument.finishPage(currentPage)
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = pdfDocument.startPage(currentPageInfo)
                currentCanvas = currentPage.canvas
                currentY = MARGIN_TOP
                drawHeaderAndFooter(currentCanvas, pageNumber)
            }

            currentCanvas.drawText("5.0 Traceability Matrix (Use Cases to Requirements)", MARGIN_LEFT, currentY, h1Paint)
            currentY += 25f

            for (uc in useCases) {
                if (currentY > MARGIN_BOTTOM - 50f) {
                    pdfDocument.finishPage(currentPage)
                    pageNumber++
                    currentPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    currentPage = pdfDocument.startPage(currentPageInfo)
                    currentCanvas = currentPage.canvas
                    currentY = MARGIN_TOP
                    drawHeaderAndFooter(currentCanvas, pageNumber)
                }
                currentCanvas.drawText("${uc.useCaseId}: ${uc.title} (Actor: ${uc.primaryActor})", MARGIN_LEFT, currentY, h2Paint)
                currentY += 16f
                currentCanvas.drawText("Traced Requirements: ${uc.linkedRequirementIds.ifBlank { "None" }}", MARGIN_LEFT + 12f, currentY, metaPaint)
                currentY += 22f
            }
        }

        pdfDocument.finishPage(currentPage)

        // Write to output file in cacheDir
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeTitle = project.keyPrefix.filter { it.isLetterOrDigit() }
        val outputFile = File(exportDir, "SRS_${safeTitle}_v${document.version}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val result = mutableListOf<String>()
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
            val words = paragraph.split(" ")
            var currentLine = StringBuilder()

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val textWidth = paint.measureText(testLine)
                if (textWidth <= maxWidth) {
                    currentLine = StringBuilder(testLine)
                } else {
                    if (currentLine.isNotEmpty()) {
                        result.add(currentLine.toString())
                    }
                    currentLine = StringBuilder(word)
                }
            }
            if (currentLine.isNotEmpty()) {
                result.add(currentLine.toString())
            }
        }
        return result
    }
}
