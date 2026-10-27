package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CutePink
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.NovaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Advanced Ultra-Cute 3D NOVA Robo Avatar
 * Features:
 * - Kawaii floating physics & playful head tilt
 * - Anime-style natural eye blinking with sparkling pupils
 * - Expressive glowing rosy cheeks with blush marks
 * - Orbiting holographic satellite stars & floating mini hearts
 * - Interactive tap reaction with happy bounce and cute speech bubbles
 * - Adaptive light/dark theme contrast
 */
@Composable
fun RobotAvatar(
    size: Dp = 120.dp,
    isThinking: Boolean = false,
    isSpeaking: Boolean = false,
    moodEmoji: String = "✨",
    showMoodBadge: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = NovaTheme.isDark
    val scope = rememberCoroutineScope()
    val tapScale = remember { Animatable(1f) }
    var showHeartBubble by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "cute_robot_anim")

    // Cute gentle levitation float
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Playful curious head tilt
    val headTilt by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_tilt"
    )

    // Pulsing cyber aura
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 600 else if (isThinking) 850 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )

    // Rotating holographic satellite orbit
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThinking) 3600 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotation"
    )

    // Blushing cute cheeks pulse
    val blushAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 450 else 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blush_alpha"
    )

    // Natural anime eye blink cycle (blinks every ~3.6 seconds)
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(140, delayMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_cycle"
    )

    // Floating sparkle particle phase
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    Box(
        modifier = modifier
            .size(size)
            .offset(y = floatOffset.dp)
            .rotate(headTilt)
            .scale(tapScale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    showHeartBubble = true
                    tapScale.animateTo(1.18f, spring(dampingRatio = 0.4f, stiffness = 600f))
                    tapScale.animateTo(1f, spring(dampingRatio = 0.6f))
                    onClick?.invoke()
                    delay(2000)
                    showHeartBubble = false
                }
            }
            .testTag("robot_avatar"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing neon aura
        Box(
            modifier = Modifier
                .size(size * auraScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            (if (isDark) CyanGlow else CyanPrimary).copy(alpha = if (isSpeaking) 0.35f else 0.18f),
                            CutePink.copy(alpha = if (isSpeaking) 0.22f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Holographic Rotating Orbit Ring with Sparkles
        Canvas(
            modifier = Modifier
                .size(size * 1.10f)
                .rotate(orbitRotation)
        ) {
            val ringWidth = 2.dp.toPx()
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        (if (isDark) CyanGlow else CyanPrimary).copy(alpha = 0.85f),
                        CutePink.copy(alpha = 0.80f),
                        Color.Transparent,
                        NeonTeal.copy(alpha = 0.85f),
                        CutePink.copy(alpha = 0.60f),
                        (if (isDark) CyanGlow else CyanPrimary).copy(alpha = 0.85f)
                    )
                ),
                radius = this.size.width / 2f - ringWidth,
                style = Stroke(width = ringWidth)
            )

            // Sparkle satellite star 1
            val angle1 = Math.toRadians(orbitRotation.toDouble())
            val orbitRadius = this.size.width / 2f - ringWidth
            val sat1X = this.size.width / 2f + (cos(angle1) * orbitRadius).toFloat()
            val sat1Y = this.size.height / 2f + (sin(angle1) * orbitRadius).toFloat()
            drawCircle(
                color = if (isDark) Color.White else CyanPrimary,
                radius = 3.2.dp.toPx(),
                center = Offset(sat1X, sat1Y)
            )

            // Sparkle satellite star 2 (opposite side in pink)
            val angle2 = angle1 + Math.PI
            val sat2X = this.size.width / 2f + (cos(angle2) * orbitRadius).toFloat()
            val sat2Y = this.size.height / 2f + (sin(angle2) * orbitRadius).toFloat()
            drawCircle(
                color = CutePink,
                radius = 2.4.dp.toPx(),
                center = Offset(sat2X, sat2Y)
            )
        }

        // Cute Robot Head Container with 3D Gloss Border
        Box(
            modifier = Modifier
                .size(size * 0.88f)
                .clip(CircleShape)
                .border(
                    2.2.dp,
                    Brush.sweepGradient(
                        listOf(
                            if (isDark) CyanGlow else CyanPrimary,
                            CutePink,
                            NeonTeal,
                            if (isDark) CyanGlow else CyanPrimary
                        )
                    ),
                    CircleShape
                )
                .background(if (isDark) DeepNavy else Color(0xFFF0F8FA)),
            contentAlignment = Alignment.Center
        ) {
            // Cute Nova 3D Robo character image
            Image(
                painter = painterResource(id = R.drawable.ic_cute_nova_robo),
                contentDescription = "Cute NOVA Robot Assistant",
                modifier = Modifier
                    .size(size * 0.86f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Kawaii dynamic overlay: Blinking anime eye reflection & Rosy blushing cheeks
            Canvas(
                modifier = Modifier
                    .size(size * 0.86f)
            ) {
                val w = this.size.width
                val h = this.size.height

                // Cheeks center coordinates
                val leftCheek = Offset(w * 0.31f, h * 0.60f)
                val rightCheek = Offset(w * 0.69f, h * 0.60f)
                val cheekRadius = w * 0.12f

                // Blushing rosy cheeks glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CutePink.copy(alpha = blushAlpha * 0.40f),
                            Color.Transparent
                        ),
                        center = leftCheek,
                        radius = cheekRadius
                    ),
                    radius = cheekRadius,
                    center = leftCheek
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CutePink.copy(alpha = blushAlpha * 0.40f),
                            Color.Transparent
                        ),
                        center = rightCheek,
                        radius = cheekRadius
                    ),
                    radius = cheekRadius,
                    center = rightCheek
                )

                // Anime blush hash marks (///) on each cheek for extra cuteness
                val slashColor = CutePink.copy(alpha = (blushAlpha * 0.7f).coerceIn(0f, 1f))
                val slashLen = w * 0.045f
                for (i in -1..1) {
                    val offsetX = i * (w * 0.032f)
                    drawLine(
                        color = slashColor,
                        start = Offset(leftCheek.x + offsetX - slashLen * 0.5f, leftCheek.y - slashLen * 0.5f),
                        end = Offset(leftCheek.x + offsetX + slashLen * 0.5f, leftCheek.y + slashLen * 0.5f),
                        strokeWidth = 1.4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = slashColor,
                        start = Offset(rightCheek.x + offsetX - slashLen * 0.5f, rightCheek.y - slashLen * 0.5f),
                        end = Offset(rightCheek.x + offsetX + slashLen * 0.5f, rightCheek.y + slashLen * 0.5f),
                        strokeWidth = 1.4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // If currently blinking, draw cute anime eyelid arc
                if (blinkProgress < 0.6f) {
                    val eyeY = h * 0.46f
                    val leftEyeX = w * 0.38f
                    val rightEyeX = w * 0.62f
                    val eyeWidth = w * 0.12f

                    // Left happy eyelid arch ◠
                    drawArc(
                        color = if (isDark) Color(0xFF00E5FF) else Color(0xFF008394),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(leftEyeX - eyeWidth / 2f, eyeY - (eyeWidth * 0.4f)),
                        size = androidx.compose.ui.geometry.Size(eyeWidth, eyeWidth * 0.65f),
                        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Right happy eyelid arch ◠
                    drawArc(
                        color = if (isDark) Color(0xFF00E5FF) else Color(0xFF008394),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(rightEyeX - eyeWidth / 2f, eyeY - (eyeWidth * 0.4f)),
                        size = androidx.compose.ui.geometry.Size(eyeWidth, eyeWidth * 0.65f),
                        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Cute holographic audio pulse waves when speaking
                if (isSpeaking) {
                    val waveY = h * 0.74f
                    val waveWidth = w * 0.28f
                    val waveCenter = Offset(w * 0.5f, waveY)
                    drawLine(
                        color = if (isDark) CyanGlow else CyanPrimary,
                        start = Offset(waveCenter.x - waveWidth / 2f, waveY),
                        end = Offset(waveCenter.x + waveWidth / 2f, waveY),
                        strokeWidth = 2.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Floating Kawaii orbiting particle hearts/stars
        Canvas(modifier = Modifier.size(size * 1.25f)) {
            val pRadius = size.toPx() * 0.52f
            val pX = center.x + cos(particlePhase) * pRadius
            val pY = center.y + sin(particlePhase) * (pRadius * 0.5f)
            drawCircle(
                color = CutePink.copy(alpha = 0.75f),
                radius = 2.dp.toPx(),
                center = Offset(pX, pY)
            )
        }

        // Interactive Happy Thought Bubble when tapped
        if (showHeartBubble) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) DeepNavy else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    CutePink
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-28).dp)
            ) {
                Text(
                    text = "💖 Nova loves you! ✨",
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Cute Mood Badge (e.g. 💖, 🎙️, 🧠, ✨)
        if (showMoodBadge && size >= 50.dp) {
            Surface(
                shape = CircleShape,
                color = if (isDark) DeepNavy else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.6.dp,
                    if (isSpeaking) CutePink else if (isDark) CyanGlow else CyanPrimary
                ),
                shadowElevation = if (isDark) 0.dp else 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp)
            ) {
                Text(
                    text = if (isSpeaking) "🎙️" else if (isThinking) "🧠" else if (moodEmoji == "⚡") "💖" else moodEmoji,
                    fontSize = (size.value * 0.17f).coerceIn(12f, 22f).sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}
