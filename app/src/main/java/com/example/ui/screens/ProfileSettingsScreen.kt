package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.viewmodel.SpecForgeViewModel

@Composable
fun ProfileSettingsScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Profile & Engineering Architecture",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Founder credentials, professional bio, and specification metadata",
            fontSize = 12.sp,
            color = SlateLight
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Founder & CEO Hero Bio Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(NavySurface, NavyCard.copy(alpha = 0.8f))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "EA",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Evans Ayanlude Abayomi",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = "Founder & CEO, WebForge Team",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElectricBlueLight
                                    )
                                    Text(
                                        text = "Cyber Security Science Student • LAUTECH, Nigeria",
                                        fontSize = 11.sp,
                                        color = SlateLight
                                    )
                                    Text(
                                        text = "Computing for Global Advancements",
                                        fontSize = 10.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = AccentCyan
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Professional Mission Statement
                            Surface(
                                color = NavyDark,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Professional Identity:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlueLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Founder & CEO, WebForge Team | Cyber Security Science Student | Full-Stack Developer | Junior Cybersecurity Analyst | Cybersecurity Defense Analyst | Ethical Hacking Enthusiast.",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Motto: Build • Secure • Innovate.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Full Professional Biography
                            Text(
                                text = "Professional Biography — Evans Ayanlude Abayomi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlueLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Evans Ayanlude Abayomi is a Cyber Security Science student at Ladoke Akintola University of Technology (LAUTECH), Ogbomoso, Nigeria, a full-stack developer, and the Founder & CEO of WebForge Team, a technology initiative driven by the mission of Computing for Global Advancements.\n\nHe is passionate about cybersecurity, software engineering, and the development of secure, reliable, and innovative digital solutions. His areas of interest include ethical hacking, cybersecurity defense, security analysis, incident response, digital forensics, cyber law, and cybercrime investigation.\n\nAs a full-stack developer, Evans works with technologies including HTML, CSS, JavaScript, React.js, Node.js, TypeScript, Python, Java, C, C++, C#, React Native, and MySQL. He is interested in building practical software applications, modern websites, and secure digital platforms that solve real-world problems.\n\nEvans has completed professional learning paths and training in Junior Cybersecurity Analysis, Cybersecurity Defense Analysis, Ethical Hacking, Endpoint Security, Network Defense, Cyber Threat Management, and Digital Forensics. He continues to develop his technical knowledge and practical skills in cybersecurity and software development.\n\nThrough WebForge Team, he seeks to encourage collaboration, develop technical talent, and promote innovation through technology. His approach combines software development with security-conscious engineering, emphasizing the importance of building systems that are not only functional but also secure and dependable.\n\nHis long-term interests include cybersecurity operations, digital forensic investigations, cybercrime investigation, and the development of technology-driven solutions that contribute to a safer digital environment.",
                                fontSize = 12.sp,
                                color = SlateLight,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Short Bio Card
                            Surface(
                                color = NavyCard,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Short Bio",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentCyan
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Evans Ayanlude Abayomi is a Cyber Security Science student at LAUTECH, full-stack developer, and Founder & CEO of WebForge Team. He specializes in building digital solutions while developing expertise in cybersecurity defense, ethical hacking, security analysis, and digital forensics. Passionate about secure innovation, he believes in using technology to solve real-world problems.\n\nBuild • Secure • Innovate.",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Technical competencies
                            Text(
                                text = "Technologies & Languages:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "HTML, CSS, JavaScript, React.js, Node.js, TypeScript, Python, Java, C, C++, C#, React Native, MySQL",
                                fontSize = 11.sp,
                                color = AccentCyan
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Cybersecurity Specializations
                            Text(
                                text = "Cybersecurity Certifications & Training:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Junior Cybersecurity Analysis, Cybersecurity Defense Analysis, Ethical Hacking, Endpoint Security, Network Defense, Cyber Threat Management, Digital Forensics",
                                fontSize = 11.sp,
                                color = AccentEmerald
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interactive Links to Portfolio & Socials
                            Text(
                                text = "Portfolio & Professional Profiles",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = { openUrl("https://portfolio-eta-ruddy-2faqad8xdz.vercel.app/") },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlueLight),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Portfolio", fontSize = 11.sp, color = ElectricBlueLight, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { openUrl("https://www.linkedin.com/in/evans-ayanlude-7194b833b") },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("LinkedIn", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { openUrl("https://github.com/EvansA20") },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateLight),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("GitHub", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = NavyCard,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { openUrl("https://x.com/Spectrehex001") }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("X (Twitter):", fontSize = 10.sp, color = SlateLight)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("@Spectrehex001", fontSize = 10.sp, color = ElectricBlueLight, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    color = NavyCard,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { openUrl("https://www.instagram.com/quantumspectre001") }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("Instagram:", fontSize = 10.sp, color = SlateLight)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("@quantumspectre001", fontSize = 10.sp, color = Color(0xFFE1306C), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // DataStore Offline Cache & Synchronization Control
            item {
                val isOnline by viewModel.isOnline.collectAsState()
                val isSyncing by viewModel.isSyncing.collectAsState()
                val lastSync by viewModel.lastSyncTime.collectAsState()
                val pendingCount by viewModel.pendingMutationsCount.collectAsState()
                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm:ss", java.util.Locale.getDefault())

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) AccentEmerald else Color(0xFFF59E0B))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DataStore Cache & Offline Sync",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                color = if (isOnline) AccentEmerald.copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) AccentEmerald else Color(0xFFF59E0B),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        SettingItemRow(
                            title = "DataStore Cache Status",
                            value = if (isOnline) "Active (Live Synchronization)" else "Offline Cache Active (Read/Write Protected)"
                        )

                        SettingItemRow(
                            title = "Last DataStore Snapshot Sync",
                            value = if (lastSync > 0) dateFormat.format(java.util.Date(lastSync)) else "Pending Initial Sync"
                        )

                        SettingItemRow(
                            title = "Pending Offline Mutations",
                            value = if (pendingCount > 0) "$pendingCount changes awaiting reconnection" else "0 pending (Fully synced)"
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.toggleSimulateOffline() },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (!isOnline) AccentEmerald else Color(0xFFF59E0B)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (!isOnline) "Go Online" else "Simulate Offline",
                                    fontSize = 11.sp,
                                    color = if (!isOnline) AccentEmerald else Color(0xFFF59E0B),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { viewModel.syncNow() },
                                enabled = !isSyncing,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isSyncing) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(12.dp),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Standards & Compliance Settings
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Specification Defaults", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        SettingItemRow(
                            title = "Active Standards Framework",
                            value = "IEEE Std 830-1998 & ISO/IEC/IEEE 29148:2018"
                        )
                        SettingItemRow(
                            title = "Engineering Initiative",
                            value = "WebForge Team — Computing for Global Advancements"
                        )
                        SettingItemRow(
                            title = "Default Document Classification",
                            value = "Confidential - Engineering Internal"
                        )
                        SettingItemRow(
                            title = "Export Engines",
                            value = "Native Android PDF + OpenXML DOCX"
                        )
                    }
                }
            }

            // Account Actions
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { viewModel.quickDemoLogin() },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlueLight),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Session", fontSize = 11.sp, color = ElectricBlueLight)
                    }

                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRose.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRose),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = AccentRose, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Out", fontSize = 11.sp, color = AccentRose, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItemRow(title: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontSize = 11.sp, color = SlateLight, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
    }
}
