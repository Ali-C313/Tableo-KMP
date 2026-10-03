package com.example.tableo.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.resources.Res
import com.example.tableo.resources.splash_logo
import com.example.tableo.ui.util.rememberLocationPermissionRequest
import com.example.tableo.data.repository.mock.OnboardingRepositoryMock
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Onboarding beim allerersten App-Start: Willkommen, Cookie-Hinweis, Standort-Freigabe.
 * Wird über [OnboardingRepository] danach nie wieder gezeigt (siehe SplashViewModel).
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    onboardingViewModel: OnboardingViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // ob erlaubt oder abgelehnt spielt hier keine Rolle, es geht in jedem Fall weiter
    val requestLocation = rememberLocationPermissionRequest {
        onboardingViewModel.completeOnboarding(onFinished)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    image = {
                        Image(
                            painter = painterResource(Res.drawable.splash_logo),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                    },
                    title = "Willkommen",
                    description = "Bestelle bei deinen Lieblingsrestaurants – am Tisch oder zum Abholen.",
                    primaryButtonText = "Los geht's",
                    onPrimaryClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                )
                1 -> OnboardingPage(
                    image = { Text("🍪", style = MaterialTheme.typography.displayLarge) },
                    title = "Deine Cookie-Einstellungen",
                    description = "Wir nutzen Cookies, um dir die App bereitzustellen. Du kannst das jederzeit in den Einstellungen ändern.",
                    primaryButtonText = "Alle erlauben",
                    onPrimaryClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    secondaryButtonText = "Nur notwendig",
                    onSecondaryClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                )
                2 -> OnboardingPage(
                    image = { Text("📍", style = MaterialTheme.typography.displayLarge) },
                    title = "Wo genau befindest du dich?",
                    description = "Teile deinen Standort, damit wir dir Restaurants in deiner Nähe zeigen können.",
                    primaryButtonText = "Standort teilen",
                    onPrimaryClick = requestLocation,
                    secondaryButtonText = "Überspringen",
                    onSecondaryClick = { onboardingViewModel.completeOnboarding(onFinished) }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            repeat(3) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    image: @Composable () -> Unit,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        image()
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            onClick = onPrimaryClick,
            shape = AppShape.Small,
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text(primaryButtonText) }
        if (secondaryButtonText != null) {
            OutlinedButton(
                onClick = onSecondaryClick,
                shape = AppShape.Small,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) { Text(secondaryButtonText) }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    TableoTheme {
        OnboardingScreen(
            onFinished = {},
            onboardingViewModel = OnboardingViewModel(OnboardingRepositoryMock())
        )
    }
}