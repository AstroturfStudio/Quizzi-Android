package studio.astroturf.quizzi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.theme.BigLogo
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.R as uiR

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizziTheme {
                SplashScreen()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Primary),
    ) {
        SplashLogo(modifier = Modifier.align(Alignment.Center))

        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            color = Color.White,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun SplashLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(id = uiR.drawable.quizzi),
            contentDescription = "Quizzi Logo",
            modifier = Modifier.size(100.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.height(50.dp),
            text = "Quizzi",
            style = BigLogo.copy(color = Color.White),
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    QuizziTheme {
        SplashScreen()
    }
}
