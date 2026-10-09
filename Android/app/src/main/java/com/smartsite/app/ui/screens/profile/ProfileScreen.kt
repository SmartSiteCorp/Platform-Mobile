package com.smartsite.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartsite.app.data.repository.Repositories
import com.smartsite.app.data.model.UserRole
import com.smartsite.app.ui.theme.AccentAmber
import com.smartsite.app.ui.theme.AccentBlue
import com.smartsite.app.ui.theme.AccentGreen
import com.smartsite.app.ui.theme.AccentPurple
import com.smartsite.app.ui.theme.AccentRose
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.frenchLabel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val DisabledFieldGray = Color(0xFFEFEDE8)

private fun roleTint(role: UserRole): Color = when (role) {
    UserRole.ADMIN -> AccentPurple
    UserRole.ARCHITECT -> AccentBlue
    UserRole.WORKER -> AccentAmber
    UserRole.PROJECT_MANAGER -> AccentGreen
    UserRole.DRONE_OPERATOR -> AccentRose
}

@Composable
fun ProfileScreen() {
    val user by Repositories.auth.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    var phone by remember(user.phone) { mutableStateOf(user.phone) }
    var specialty by remember(user.specialty) { mutableStateOf(user.specialty) }
    var saving by remember { mutableStateOf(false) }

    val tint = roleTint(user.role)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 16.dp)
    ) {
        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(tint),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.fullName.first().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Shield,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = user.role.frenchLabel(),
                            color = tint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Form card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Informations personnelles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = user.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    enabled = false,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = DisabledFieldGray,
                        disabledTextColor = TextMuted,
                        disabledLabelColor = TextMuted,
                        disabledBorderColor = BorderLight
                    )
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone") },
                    placeholder = { Text("06 00 00 00 00") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Spécialité") },
                    placeholder = { Text("ex: Maçonnerie, Plomberie...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        scope.launch {
                            saving = true
                            delay(600)
                            Repositories.auth.update(
                                user.copy(phone = phone.trim(), specialty = specialty.trim())
                            )
                            saving = false
                        }
                    },
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    )
                ) {
                    if (saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Enregistrement...")
                    } else {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Enregistrer")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
