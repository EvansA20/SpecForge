package com.example.data.export

import android.content.Context
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
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DocxExporter {

    fun generateDocx(
        context: Context,
        project: Project,
        document: SrsDocument,
        sections: List<DocumentSection>,
        requirements: List<Requirement>,
        useCases: List<UseCase>
    ): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeTitle = project.keyPrefix.filter { it.isLetterOrDigit() }
        val outputFile = File(exportDir, "SRS_${safeTitle}_v${document.version}.docx")

        val docXml = buildDocumentXml(project, document, sections, requirements, useCases)
        val contentTypesXml = buildContentTypesXml()
        val relsXml = buildRelsXml()
        val docRelsXml = buildDocumentRelsXml()
        val stylesXml = buildStylesXml()

        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            // 1. [Content_Types].xml
            zip.putNextEntry(ZipEntry("[Content_Types].xml"))
            zip.write(contentTypesXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // 2. _rels/.rels
            zip.putNextEntry(ZipEntry("_rels/.rels"))
            zip.write(relsXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // 3. word/_rels/document.xml.rels
            zip.putNextEntry(ZipEntry("word/_rels/document.xml.rels"))
            zip.write(docRelsXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // 4. word/styles.xml
            zip.putNextEntry(ZipEntry("word/styles.xml"))
            zip.write(stylesXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // 5. word/document.xml
            zip.putNextEntry(ZipEntry("word/document.xml"))
            zip.write(docXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }

        return outputFile
    }

    private fun escapeXml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun buildDocumentXml(
        project: Project,
        document: SrsDocument,
        sections: List<DocumentSection>,
        requirements: List<Requirement>,
        useCases: List<UseCase>
    ): String {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val pubDate = dateFormat.format(Date(document.lastModified))

        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">""")
        sb.append("<w:body>")

        // Document Title
        sb.append("<w:p>")
        sb.append("<w:pPr><w:jc w:val=\"center\"/><w:spacing w:after=\"120\"/></w:pPr>")
        sb.append("<w:r><w:rPr><w:b/><w:color w:val=\"0066FF\"/><w:sz w:val=\"24\"/></w:rPr><w:t>SOFTWARE REQUIREMENTS SPECIFICATION</w:t></w:r>")
        sb.append("</w:p>")

        // Project Title
        sb.append("<w:p>")
        sb.append("<w:pPr><w:jc w:val=\"center\"/><w:spacing w:after=\"160\"/></w:pPr>")
        sb.append("<w:r><w:rPr><w:b/><w:color w:val=\"0B132B\"/><w:sz w:val=\"44\"/></w:rPr><w:t>${escapeXml(project.title)}</w:t></w:r>")
        sb.append("</w:p>")

        // Document Subtitle
        sb.append("<w:p>")
        sb.append("<w:pPr><w:jc w:val=\"center\"/><w:spacing w:after=\"300\"/></w:pPr>")
        sb.append("<w:r><w:rPr><w:i/><w:color w:val=\"334155\"/><w:sz w:val=\"28\"/></w:rPr><w:t>${escapeXml(document.title)} — Version ${escapeXml(document.version)}</w:t></w:r>")
        sb.append("</w:p>")

        // Metadata Table
        sb.append("<w:tbl>")
        sb.append("<w:tblPr><w:tblW w:w=\"9000\" w:type=\"dxa\"/><w:jc w:val=\"center\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CBD5E1\"/><w:left w:val=\"none\"/><w:bottom w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CBD5E1\"/><w:right w:val=\"none\"/><w:insideH w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:insideV w:val=\"none\"/></w:tblBorders></w:tblPr>")

        val metaPairs = listOf(
            "Project Key" to project.keyPrefix,
            "Category" to project.category,
            "Document Status" to document.status,
            "Author / Architect" to document.author,
            "Organization" to document.organization,
            "Platform Architect" to "Evans Ayanlude Abayomi (WebForge Team)",
            "Classification" to document.confidentiality,
            "Publication Date" to pubDate
        )

        for ((field, value) in metaPairs) {
            sb.append("<w:tr>")
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"3000\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:rPr><w:b/><w:color w:val=\"475569\"/></w:rPr><w:t>${escapeXml(field)}</w:t></w:r></w:p></w:tc>")
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"6000\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:t>${escapeXml(value)}</w:t></w:r></w:p></w:tc>")
            sb.append("</w:tr>")
        }
        sb.append("</w:tbl>")

        // Page break after cover
        sb.append("<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>")

        // Executive Overview
        sb.append("<w:p><w:pPr><w:pStyle w:val=\"Heading1\"/></w:pPr><w:r><w:t>Project Overview</w:t></w:r></w:p>")
        for (para in project.description.split("\n")) {
            if (para.isNotBlank()) {
                sb.append("<w:p><w:r><w:t>${escapeXml(para)}</w:t></w:r></w:p>")
            }
        }

        // Render Sections
        for (section in sections.filter { it.isEnabled }) {
            sb.append("<w:p><w:pPr><w:pStyle w:val=\"Heading1\"/><w:spacing w:before=\"240\" w:after=\"120\"/></w:pPr><w:r><w:t>${escapeXml(section.sectionNumber)} ${escapeXml(section.title)}</w:t></w:r></w:p>")
            for (para in section.content.split("\n")) {
                if (para.isNotBlank()) {
                    sb.append("<w:p><w:pPr><w:spacing w:after=\"100\"/></w:pPr><w:r><w:t>${escapeXml(para)}</w:t></w:r></w:p>")
                }
            }
        }

        // Requirements Table
        sb.append("<w:p><w:pPr><w:pStyle w:val=\"Heading1\"/><w:spacing w:before=\"280\" w:after=\"140\"/></w:pPr><w:r><w:t>System Requirements Specification Table</w:t></w:r></w:p>")

        sb.append("<w:tbl>")
        sb.append("<w:tblPr><w:tblW w:w=\"9500\" w:type=\"dxa\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"0066FF\"/><w:bottom w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"0066FF\"/><w:left w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:right w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:insideH w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:insideV w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/></w:tblBorders></w:tblPr>")

        // Header Row
        sb.append("<w:tr>")
        val headers = listOf("ID" to 1500, "Title & Specification Statement" to 5000, "Type / Priority" to 1500, "Verification" to 1500)
        for ((hText, width) in headers) {
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"$width\" w:type=\"dxa\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F1F5F9\"/></w:tcPr><w:p><w:r><w:rPr><w:b/><w:color w:val=\"0B132B\"/></w:rPr><w:t>${escapeXml(hText)}</w:t></w:r></w:p></w:tc>")
        }
        sb.append("</w:tr>")

        // Data Rows
        for (req in requirements) {
            sb.append("<w:tr>")
            // ID
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"1500\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:rPr><w:b/><w:color w:val=\"0066FF\"/></w:rPr><w:t>${escapeXml(req.reqId)}</w:t></w:r></w:p></w:tc>")
            // Statement + Acceptance
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"5000\" w:type=\"dxa\"/></w:tcPr>")
            sb.append("<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>${escapeXml(req.title)}</w:t></w:r></w:p>")
            sb.append("<w:p><w:r><w:t>${escapeXml(req.statement)}</w:t></w:r></w:p>")
            if (req.acceptanceCriteria.isNotBlank()) {
                sb.append("<w:p><w:r><w:rPr><w:i/><w:color w:val=\"64748B\"/></w:rPr><w:t>Criteria: ${escapeXml(req.acceptanceCriteria)}</w:t></w:r></w:p>")
            }
            sb.append("</w:tc>")
            // Type & Priority
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"1500\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:t>${escapeXml(req.type)} (${escapeXml(req.priority)})</w:t></w:r></w:p></w:tc>")
            // Verification Method
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"1500\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:t>${escapeXml(req.verificationMethod)}</w:t></w:r></w:p></w:tc>")
            sb.append("</w:tr>")
        }
        sb.append("</w:tbl>")

        // Traceability Matrix Table
        if (useCases.isNotEmpty()) {
            sb.append("<w:p><w:pPr><w:pStyle w:val=\"Heading1\"/><w:spacing w:before=\"280\" w:after=\"140\"/></w:pPr><w:r><w:t>Use Cases & Traceability Matrix</w:t></w:r></w:p>")
            sb.append("<w:tbl>")
            sb.append("<w:tblPr><w:tblW w:w=\"9500\" w:type=\"dxa\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"0066FF\"/><w:bottom w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"0066FF\"/><w:left w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:right w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:insideH w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/><w:insideV w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"E2E8F0\"/></w:tblBorders></w:tblPr>")

            sb.append("<w:tr>")
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"2000\" w:type=\"dxa\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F1F5F9\"/></w:tcPr><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Use Case ID</w:t></w:r></w:p></w:tc>")
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"4500\" w:type=\"dxa\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F1F5F9\"/></w:tcPr><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Title & Actor</w:t></w:r></w:p></w:tc>")
            sb.append("<w:tc><w:tcPr><w:tcW w:w=\"3000\" w:type=\"dxa\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F1F5F9\"/></w:tcPr><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Linked Requirements</w:t></w:r></w:p></w:tc>")
            sb.append("</w:tr>")

            for (uc in useCases) {
                sb.append("<w:tr>")
                sb.append("<w:tc><w:tcPr><w:tcW w:w=\"2000\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:rPr><w:b/><w:color w:val=\"0066FF\"/></w:rPr><w:t>${escapeXml(uc.useCaseId)}</w:t></w:r></w:p></w:tc>")
                sb.append("<w:tc><w:tcPr><w:tcW w:w=\"4500\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:t>${escapeXml(uc.title)} (${escapeXml(uc.primaryActor)})</w:t></w:r></w:p></w:tc>")
                sb.append("<w:tc><w:tcPr><w:tcW w:w=\"3000\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:t>${escapeXml(uc.linkedRequirementIds.ifBlank { "None" })}</w:t></w:r></w:p></w:tc>")
                sb.append("</w:tr>")
            }
            sb.append("</w:tbl>")
        }

        sb.append("</w:body>")
        sb.append("</w:document>")
        return sb.toString()
    }

    private fun buildContentTypesXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>"""
    }

    private fun buildRelsXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""
    }

    private fun buildDocumentRelsXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""
    }

    private fun buildStylesXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal">
    <w:name w:val="Normal"/>
    <w:rPr>
      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
      <w:sz w:val="22"/>
      <w:color w:val="1E293B"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading1">
    <w:name w:val="heading 1"/>
    <w:rPr>
      <w:b/>
      <w:color w:val="0066FF"/>
      <w:sz w:val="32"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading2">
    <w:name w:val="heading 2"/>
    <w:rPr>
      <w:b/>
      <w:color w:val="0B132B"/>
      <w:sz w:val="26"/>
    </w:rPr>
  </w:style>
</w:styles>"""
    }
}
