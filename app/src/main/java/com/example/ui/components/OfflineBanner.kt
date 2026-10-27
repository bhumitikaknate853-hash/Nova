package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaTheme

/**
 * A subtle, non-intrusive 'Offline' banner displayed when the app detects no internet connection.
 */
@Composable
fun OfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = NovaTheme.isDark
    val themeVars = NovaTheme.vars

    AnimatedVisibility(
        visible = isOffline,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        val bannerBg = if (isDark) Color(0x33FFB300) else Color(0x22F57C00)
        val bannerBorder = if (isDark) Color(0x66FFB300) else Color(0x44F57C00)
        val textCol = if (isDark) Color(0xFFFFD54F) else Color(0xFFE65100)
        val dotCol = if (isDark) Color(0xFFFFB300) else Color(0xFFFF6D00)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bannerBg)
                .border(1.dp, bannerBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .testTag("offline_banner"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Pulsing dot indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotCol)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📡",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Offline Mode",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textCol
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• Using local AI memory & cache",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = textCol.copy(alpha = 0.9f)
                )
            }
        }
    }
}
