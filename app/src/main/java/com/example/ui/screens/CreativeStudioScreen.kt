package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CreationItem
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.ShareAppDialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NovaTheme
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreativeStudioScreen(
    creations: List<CreationItem>,
    currentGeneratedBitmap: Bitmap?,
    isGenerating: Boolean,
    onGenerateImage: (prompt: String, style: String, aspectRatio: String) -> Unit,
    onSaveCreation: (prompt: String, style: String, aspectRatio: String, bitmap: Bitmap) -> Unit,
    onDeleteCreation: (creationId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var promptText by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") }
    var showShareDialog by remember { mutableStateOf(false) }
    val themeVars = NovaTheme.vars

    val styles = listOf(
        "Cyberpunk" to "⚡ Cyberpunk",
        "Photorealistic" to "📸 Realistic",
        "Anime" to "🌸 Anime",
        "Digital Art" to "🎨 Digital Art",
        "Surreal" to "🌀 Surreal",
        "Oil Painting" to "🖌️ Oil Painting"
    )

    val aspectRatios = listOf("1:1", "16:9", "9:16", "4:3")

    AnimatedCyberBackground(isDarkTheme = themeVars.isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("creative_studio_screen")
        ) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NOVA CREATIVE STUDIO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("studio_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = themeVars.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.testTag("studio_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = if (themeVars.isDark) CyanGlow else themeVars.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Prompt Editor Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("studio_prompt_card"),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = themeVars.surfaceCard,
                    contentPadding = 14.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✨", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CREATIVE PROMPT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeVars.sectionHeader,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Prompt enhancement button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (themeVars.isDark) Color(0x22E040FB) else Color(0xFFF3E5F5),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (themeVars.isDark) Color(0x66E040FB) else Color(0xFFCE93D8)
                                ),
                                modifier = Modifier.clickable {
                                    if (promptText.isNotBlank()) {
                                        promptText = "Ultra cinematic $promptText, dramatic cyan volumetric lighting, intricate details, 8k render, octane engine masterpiece ✨"
                                    } else {
                                        promptText = "Cybernetic AI brain floating inside a holographic quantum server with glowing cyan neural pathways 🤖⚡"
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFAB47BC),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Enhance Prompt ✨",
                                        color = Color(0xFF8E24AA),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            placeholder = {
                                Text(
                                    text = "Describe what you want to imagine... (e.g., A sleek robot companion overlooking a neon futuristic Tokyo skyline 🌆)",
                                    color = themeVars.textSecondary.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("studio_prompt_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = themeVars.textPrimary,
                                unfocusedTextColor = themeVars.textPrimary,
                                focusedBorderColor = themeVars.primary,
                                unfocusedBorderColor = themeVars.surfaceBorder,
                                focusedContainerColor = themeVars.surface,
                                unfocusedContainerColor = themeVars.surface
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Style selector chips with emojis
                        Text(
                            text = "ARTISTIC STYLE 🎨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.sectionHeader,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            styles.forEach { (styleKey, styleDisplay) ->
                                val isSelected = styleKey == selectedStyle
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFFAB47BC) else themeVars.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFCE93D8) else themeVars.surfaceBorder
                                    ),
                                    modifier = Modifier.clickable { selectedStyle = styleKey }
                                ) {
                                    Text(
                                        text = styleDisplay,
                                        color = if (isSelected) Color.White else themeVars.textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Aspect Ratio selector
                        Text(
                            text = "ASPECT RATIO 📐",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.sectionHeader,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            aspectRatios.forEach { ratio ->
                                val isSelected = ratio == selectedAspectRatio
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) themeVars.primary else themeVars.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) themeVars.primaryGlow else themeVars.surfaceBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedAspectRatio = ratio }
                                ) {
                                    Text(
                                        text = ratio,
                                        color = if (isSelected) Color.White else themeVars.textSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Generate Button
                        Button(
                            onClick = {
                                if (promptText.isNotBlank() && !isGenerating) {
                                    onGenerateImage(promptText.trim(), selectedStyle, selectedAspectRatio)
                                }
                            },
                            enabled = promptText.isNotBlank() && !isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("studio_generate_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAB47BC),
                                contentColor = Color.White
                            )
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Synthesizing Canvas with Gemini... ✨", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Image 🚀", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Current Generated Result Card
                if (currentGeneratedBitmap != null) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_result_card"),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = themeVars.surfaceCard,
                        contentPadding = 14.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LATEST GENERATION ✨",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeVars.sectionHeader,
                                    letterSpacing = 1.sp
                                )

                                IconButton(
                                    onClick = {
                                        saveBitmapToGallery(context, currentGeneratedBitmap, "nova_creation_${System.currentTimeMillis()}")
                                        onSaveCreation(promptText, selectedStyle, selectedAspectRatio, currentGeneratedBitmap)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Save to Device",
                                        tint = if (themeVars.isDark) CyanGlow else themeVars.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Image(
                                bitmap = currentGeneratedBitmap.asImageBitmap(),
                                contentDescription = "Generated AI Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, themeVars.surfaceBorder, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Studio Gallery Section
                Text(
                    text = "SAVED CREATIONS (${creations.size}) 🖼️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeVars.sectionHeader,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (creations.isEmpty() && currentGeneratedBitmap == null) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = themeVars.surfaceCard,
                        contentPadding = 16.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No saved creations yet 🎨 Type a prompt above and tap Generate Image to create visual artwork with NOVA!",
                                color = themeVars.textSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (item in creations) {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                backgroundColor = themeVars.surfaceCard,
                                contentPadding = 12.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl.ifBlank { R.drawable.ic_nova_logo },
                                        contentDescription = item.prompt,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.prompt,
                                            color = themeVars.textPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 2
                                        )
                                        Text(
                                            text = "${item.style} • ${item.aspectRatio}",
                                            color = if (themeVars.isDark) CyanGlow else themeVars.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    IconButton(onClick = { onDeleteCreation(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = themeVars.textSecondary.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showShareDialog) {
        ShareAppDialog(onDismiss = { showShareDialog = false })
    }
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String) {
    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NovaAi")
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            val stream: OutputStream? = resolver.openOutputStream(uri)
            stream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Toast.makeText(context, "Image saved to device gallery! 🖼️", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
