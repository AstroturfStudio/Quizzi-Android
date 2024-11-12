package com.alicankorkmaz.quizzi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alicankorkmaz.quizzi.domain.model.ClientQuestion
import com.alicankorkmaz.quizzi.domain.model.Option
import com.alicankorkmaz.quizzi.domain.model.websocket.GameMessage.AnswerResult

@Composable
fun AnswerOptionsGrid(
    question: ClientQuestion?,
    lastAnswer: AnswerResult?,
    hasAnswered: Boolean,
    onAnswerSelected: (String) -> Unit,
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
                        text = option.name,
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
                        text = option.name,
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
        question = ClientQuestion(
            flagUrl = "https://example.com/flag.png",
            options = listOf(
                Option("1", "Türkiye"),
                Option("2", "Almanya"),
                Option("3", "Fransa"),
                Option("4", "İtalya")
            )
        ),
        lastAnswer = null,
        hasAnswered = false,
        onAnswerSelected = {}
    )
} 