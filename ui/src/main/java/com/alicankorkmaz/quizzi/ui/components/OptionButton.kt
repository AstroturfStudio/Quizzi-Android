package com.alicankorkmaz.quizzi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OptionButton(
    text: String,
    letter: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1A3A6E),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, Color(0xFF3A5894))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$letter:",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OptionButton(
            text = "Türkiye",
            letter = 'A',
            onClick = {}
        )

        OptionButton(
            text = "Almanya",
            letter = 'B',
            onClick = {}
        )
    }
}