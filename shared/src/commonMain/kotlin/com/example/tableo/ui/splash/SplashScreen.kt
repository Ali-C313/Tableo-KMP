package com.example.tableo.ui.splash

import com.example.tableo.resources.Res
import com.example.tableo.resources.splash_logo
import com.example.tableo.resources.app_name
import com.example.tableo.resources.splash_logo_description
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.OnboardingRepositoryMock
import com.example.tableo.ui.theme.TableoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel

/**
 * Kurzer Lade-Bildschirm mit App-Logo und animiertem App-Namen.
 *
 * @param onFinished Wird aufgerufen, sobald feststeht, ob als Nächstes das Onboarding
 * (erster Start) oder direkt die Restaurant-Liste kommt
 */
@Composable
fun SplashScreen(
    onFinished: (showOnboarding: Boolean) -> Unit,
    splashViewModel: SplashViewModel = koinViewModel()
) {
    var visible by remember { mutableFloatStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue = visible,
        animationSpec = tween(durationMillis = 600),
        label = "splashAlpha"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (visible == 1f) 1f else 0.85f,
        animationSpec = tween(durationMillis = 600),
        label = "splashScale"
    )

    LaunchedEffect(Unit) {
        visible = 1f
        delay(1200)
        // wartet, bis das ViewModel den DataStore ausgelesen hat (meist längst fertig)
        val ready = splashViewModel.uiState.filterIsInstance<SplashUiState.Ready>().first()
        onFinished(ready.showOnboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(animatedAlpha)
                .scale(animatedScale)
        ) {
            Image(
                painter = painterResource(Res.drawable.splash_logo),
                contentDescription = stringResource(Res.string.splash_logo_description),
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    TableoTheme {
        SplashScreen(
            onFinished = {},
            splashViewModel = SplashViewModel(OnboardingRepositoryMock(), AuthRepositoryMock())
        )
    }
}