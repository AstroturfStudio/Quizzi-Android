package com.alicankorkmaz.quizzi.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun GameBar(
    cursorPosition: Float,
    modifier: Modifier = Modifier
) {
    val cursorPositionAnimated by animateFloatAsState(
        targetValue = cursorPosition,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessLow
        )
    )

    var containerWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onSizeChanged { containerWidth = it.width }
    ) {
        // Sol taraf (Mavi)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(cursorPositionAnimated)
                .background(Color(0xFF1A3A6E))
        )

        // Sağ taraf (Kırmızı)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f - cursorPositionAnimated)
                .align(Alignment.CenterEnd)
                .background(Color(0xFFB71C1C))
        )

        // Cursor
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset {
                    IntOffset(
                        x = (cursorPositionAnimated * containerWidth).toInt() - 8.dp
                            .toPx()
                            .toInt(),
                        y = 0
                    )
                }
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameBarPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(8.dp)
    ) {
        GameBar(
            cursorPosition = 0.2f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameBarMovingPreview() {
    var position by remember { mutableStateOf(0.5f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            position = if (position < 0.8f) position + 0.1f else 0.2f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(8.dp)
    ) {
        GameBar(
            cursorPosition = position,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}