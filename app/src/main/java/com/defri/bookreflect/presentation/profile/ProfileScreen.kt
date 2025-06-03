package com.defri.bookreflect.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Surface(
                modifier = Modifier
                    .size(120.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TODO: Replace with user name
            Text(
                text = "John Doe",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TODO: Replace with user email
            Text(
                text = "john.doe@example.com",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Edit Profile") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Navigate"
                            )
                        }
                    )

                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Reading Preferences") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Reading Preferences"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Navigate"
                            )
                        }
                    )

                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Notifications") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Navigate"
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    fontSize = 18.sp
                )
            }
        }
    }
} 