package com.example.remind.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerSheet(
    onDismiss: () -> Unit,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE2F5DA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onPickGallery,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC7DCBE))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gallery),
                            contentDescription = "Галерея",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Загрузить из галереи", fontSize = 18.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    IconButton(
                        onClick = onTakePhoto,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC7DCBE))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = "Камера",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Сделать снимок", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
