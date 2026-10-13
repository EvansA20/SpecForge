package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpecForgeViewModel

enum class AuthMode {
    SIGN_IN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    var email by remember { mutableStateOf("eaayanlude@gmail.com") }
    var password by remember { mutableStateOf("demo123") }
    var fullName by remember { mutableStateOf("Evans Ayanlude Abayomi") }
    var organization by remember { mutableStateOf("WebForge Team") }
    var role by remember { mutableStateOf("Founder & CEO, WebForge Team") }
    var securityAnswer by remember { mutableStateOf("webforge") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Brand Header
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "SpecForge Platform",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Software Requirements Specification Engine",
                            fontSize = 12.sp,
                            color = ElectricBlueLight
                        )
                    }
                }

                // Auth Mode Tabs
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = if (authMode == AuthMode.SIGN_IN) ElectricBlue else NavyCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    authMode = AuthMode.SIGN_IN
                                    errorMessage = null
                                }
                                .testTag("auth_tab_signin")
                        ) {
                            Text(
                                text = "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            color = if (authMode == AuthMode.REGISTER) ElectricBlue else NavyCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    authMode = AuthMode.REGISTER
                                    errorMessage = null
                                }
                                .testTag("auth_tab_register")
                        ) {
                            Text(
                                text = "Register",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Sign In Form
                if (authMode == AuthMode.SIGN_IN) {
                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = SlateLight) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = SlateLight
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = { authMode = AuthMode.FORGOT_PASSWORD },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text("Forgot password?", color = ElectricBlueLight, fontSize = 11.sp)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.login(email.trim(), password) { success, err ->
                                    if (success) {
                                        viewModel.navigateTo(AppScreen.DASHBOARD)
                                    } else {
                                        errorMessage = err
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("login_submit_btn")
                        ) {
                            Text("Sign In to SpecForge", fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = { viewModel.quickDemoLogin() },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlueLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("login_demo_btn")
                        ) {
                            Text("Instant Demo Sign In", color = ElectricBlueLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Register Form
                if (authMode == AuthMode.REGISTER) {
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = organization,
                            onValueChange = { organization = it },
                            label = { Text("Organization / University", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = SlateLight) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = securityAnswer,
                            onValueChange = { securityAnswer = it },
                            label = { Text("Recovery Security Word (e.g. favorite animal/school)", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please enter name, email, and password."
                                } else {
                                    viewModel.register(
                                        email.trim(),
                                        password,
                                        fullName.trim(),
                                        organization.trim().ifBlank { "Independent Team" },
                                        role.trim()
                                    ) { success, err ->
                                        if (success) {
                                            viewModel.navigateTo(AppScreen.DASHBOARD)
                                        } else {
                                            errorMessage = err
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text("Create Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Forgot Password Form
                if (authMode == AuthMode.FORGOT_PASSWORD) {
                    item {
                        Text("Reset Your Password", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Verify with your account email and security recovery answer.", color = SlateLight, fontSize = 11.sp)
                    }

                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Account Email", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = securityAnswer,
                            onValueChange = { securityAnswer = it },
                            label = { Text("Security Recovery Word", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("New Password", color = SlateLight) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.resetPassword(email.trim(), password, securityAnswer.trim()) { success, err ->
                                    if (success) {
                                        authMode = AuthMode.SIGN_IN
                                        errorMessage = null
                                    } else {
                                        errorMessage = err
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text("Update Password", fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        TextButton(
                            onClick = { authMode = AuthMode.SIGN_IN },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back to Sign In", color = ElectricBlueLight)
                        }
                    }
                }

                // Error / Success notices
                if (errorMessage != null) {
                    item {
                        Surface(
                            color = AccentRose.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = AccentRose,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
