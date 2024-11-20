package com.astroturf.quizzi.ui.screen.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.astroturf.quizzi.domain.model.Option
import com.astroturf.quizzi.domain.model.Question
import com.astroturf.quizzi.domain.model.websocket.ServerMessage

@Composable
fun AnswerOptionsGrid(
    question: Question?,
    lastAnswer: ServerMessage.AnswerResult?,
    hasAnswered: Boolean,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    question?.let {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // İlk satır (A ve B şıkları)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                question.options.take(2).forEachIndexed { index, option ->
                    AnswerOptionButton(
                        text = option.value,
                        letter = ('A' + index),
                        isSelected = lastAnswer?.answer == option.id,
                        isCorrect = lastAnswer?.let { it.correct && it.answer == option.id } == true,
                        enabled = !hasAnswered,
                        onClick = { onAnswerSelected(option.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // İkinci satır (C ve D şıkları)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                question.options.drop(2).forEachIndexed { index, option ->
                    AnswerOptionButton(
                        text = option.value,
                        letter = ('C' + index),
                        isSelected = lastAnswer?.answer == option.id,
                        isCorrect = lastAnswer?.let { it.correct && it.answer == option.id } == true,
                        enabled = !hasAnswered,
                        onClick = { onAnswerSelected(option.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AnswerOptionsGridPreview() {
    AnswerOptionsGrid(
        question = Question(
            categoryId = 1,
            imageUrl = "https://example.com/flag.png",
            content = "Hangi ülkedir?",
            options = listOf(
                Option(id = 1, value = "Türkiye"),
                Option(id = 2, value = "Almanya"),
                Option(id = 3, value = "Fransa"),
                Option(
                    id = 4, value = "İtalya"
                )
            ),
        ),
        lastAnswer = null,
        hasAnswered = false,
        onAnswerSelected = {}
    )
} 