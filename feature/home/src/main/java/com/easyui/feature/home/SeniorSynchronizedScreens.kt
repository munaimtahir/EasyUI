package com.easyui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun SeniorMessagesScreen(
    onOpenMessages: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("senior_messages_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xl),
        ) {
            Text("Messages", style = MaterialTheme.typography.headlineLarge)
            
            Text(
                "You can send messages to your family and friends.",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = onOpenMessages,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Messages", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}

@Composable
fun SeniorPhotosScreen(
    onOpenPhotos: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("senior_photos_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xl),
        ) {
            Text("Photos", style = MaterialTheme.typography.headlineLarge)
            
            Text(
                "View your photos and memories here.",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = onOpenPhotos,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Photos", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}

@Composable
fun SeniorCameraScreen(
    onOpenCamera: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("senior_camera_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xl),
        ) {
            Text("Camera", style = MaterialTheme.typography.headlineLarge)
            
            Text(
                "Take a photo to capture the moment.",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = onOpenCamera,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Take a Photo", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}

@Composable
fun SafeHandoffScreen(
    actionTitle: String,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.xl)
                .testTag("safe_handoff_screen"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Opening $actionTitle",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Press the Home button at the bottom to go back when you are finished.",
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Stay here", fontSize = 22.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
