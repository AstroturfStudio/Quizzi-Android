package com.alicankorkmaz.flagquiz.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

@Composable
fun AnswerOptionButton(
    text: String,
    letter: Char,
    isSelected: Boolean,
    isCorrect: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = 0.3f,
            stiffness = 300f
        ),
        label = ""
    )

    val backgroundColor = when {
        isSelected && isCorrect -> Color.Green.copy(alpha = 0.2f)
        isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = if (isSelected) {
                if (isCorrect) Color.Green else Color.Red
            } else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .scale(scale)
    ) {
        Text(
            text = "$letter. $text",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}