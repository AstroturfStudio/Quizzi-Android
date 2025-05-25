package studio.astroturf.quizzi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
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
                SplashScreen {
                    // Navigate to MainActivity when splash is complete
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
private fun SplashScreen(onSplashComplete: () -> Unit) {
    var showAnimation by remember { mutableStateOf(true) }
    var showLogo by remember { mutableStateOf(false) }

    // Remember the latest callback to avoid issues with restarting effects
    val currentOnSplashComplete by rememberUpdatedState(onSplashComplete)

    // Load the Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(uiR.raw.splash))

    // Animation progress
    val animationProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1, // Play once
        isPlaying = showAnimation,
    )

    // Logo alpha animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "logoAlpha",
    )

    // Handle animation sequence
    LaunchedEffect(animationProgress) {
        if (animationProgress == 1f) {
            // Animation finished, show logo
            showAnimation = false
            showLogo = true

            // Wait for logo to be visible, then navigate
            delay(1500) // Show logo for 1.5 seconds
            currentOnSplashComplete()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Primary),
    ) {
        // Lottie Animation
        if (showAnimation && composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { animationProgress },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
            )
        }

        // Logo (shown after animation)
        if (showLogo) {
            SplashLogo(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .alpha(logoAlpha),
            )
        }

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
        SplashScreen { /* Preview - no navigation */ }
    }
}
