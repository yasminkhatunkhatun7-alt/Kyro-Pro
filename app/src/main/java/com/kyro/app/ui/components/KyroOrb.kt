package com.kyro.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.kyro.app.ui.theme.Amber
import com.kyro.app.ui.theme.DeepObsidian
import com.kyro.app.ui.theme.PaleGold
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class OrbState {
    IDLE, LISTENING, THINKING
}

@Composable
fun KyroOrb(
    state: OrbState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_transition")

    // Breathing animation for the core
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (state == OrbState.LISTENING) 1000 else 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    // Rotation for rings
    val rotationBase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (state == OrbState.THINKING) 3000 else 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val particles = remember { List(80) { Particle() } }

    Canvas(modifier = modifier.size(250.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 3 * breathScale

        // Deep obsidian core with glowing edges
        val coreBrush = Brush.radialGradient(
            colors = listOf(
                DeepObsidian,
                Amber.copy(alpha = if (state == OrbState.LISTENING) 0.8f else 0.4f),
                Color.Transparent
            ),
            center = center,
            radius = radius * 1.5f
        )
        drawCircle(
            brush = coreBrush,
            radius = radius * 1.5f,
            center = center
        )

        // Inner core
        drawCircle(
            color = DeepObsidian,
            radius = radius * 0.8f,
            center = center
        )

        // Rotating Wireframe Rings
        rotate(rotationBase) {
            drawCircle(
                color = PaleGold.copy(alpha = 0.5f),
                radius = radius * 1.1f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        rotate(-rotationBase * 1.5f + 45f) {
            drawCircle(
                color = Amber.copy(alpha = 0.3f),
                radius = radius * 1.25f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            // Draw a few ticks on the ring
            for (i in 0..3) {
                 val angle = (i * 90) * (Math.PI / 180)
                 val start = Offset(
                     center.x + (radius * 1.15f) * cos(angle).toFloat(),
                     center.y + (radius * 1.15f) * sin(angle).toFloat()
                 )
                 val end = Offset(
                     center.x + (radius * 1.35f) * cos(angle).toFloat(),
                     center.y + (radius * 1.35f) * sin(angle).toFloat()
                 )
                 drawLine(color = PaleGold, start = start, end = end, strokeWidth = 3f)
            }
        }

        // Orbiting Particles
        particles.forEach { particle ->
            particle.update(rotationBase)
            val pRadius = radius + particle.distanceOffset * radius
            val px = center.x + pRadius * cos(particle.currentAngle)
            val py = center.y + pRadius * sin(particle.currentAngle)

            drawCircle(
                color = Amber.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(px, py)
            )
        }
    }
}

class Particle {
    private val baseAngle = Random.nextFloat() * 2 * Math.PI.toFloat()
    val distanceOffset = Random.nextFloat() * 0.8f // 0.0 to 0.8 outside core
    private val speedMultiplier = Random.nextFloat() * 2f + 0.5f
    val size = Random.nextFloat() * 4f + 2f
    val alpha = Random.nextFloat() * 0.6f + 0.2f
    var currentAngle = 0f

    fun update(baseRotationDegree: Float) {
        // Convert base rotation to radians and apply speed
        val baseRotationRad = baseRotationDegree * (Math.PI / 180).toFloat()
        currentAngle = baseAngle + (baseRotationRad * speedMultiplier)
    }
}
