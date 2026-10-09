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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.resources.Res
import com.example.tableo.resources.onboarding_cookies_allow_all
import com.example.tableo.resources.onboarding_cookies_necessary
import com.example.tableo.resources.onboarding_cookies_text
import com.example.tableo.resources.onboarding_cookies_title
import com.example.tableo.resources.onboarding_location_share
import com.example.tableo.resources.onboarding_location_text
import com.example.tableo.resources.onboarding_location_title
import com.example.tableo.resources.onboarding_skip
import com.example.tableo.resources.onboarding_welcome_button
import com.example.tableo.resources.onboarding_welcome_text
import com.example.tableo.resources.onboarding_welcome_title
import com.example.tableo.resources.splash_logo
import com.example.tableo.util.rememberLocationPermissionRequest
import com.example.tableo.data.repository.mock.OnboardingRepositoryMock
import com.example.tableo.ui.theme.TableoTheme
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
                    title = stringResource(Res.string.onboarding_welcome_title),
                    description = stringResource(Res.string.onboarding_welcome_text),
                    primaryButtonText = stringResource(Res.string.onboarding_welcome_button),
                    onPrimaryClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                )
                1 -> OnboardingPage(
                    image = { Text("🍪", style = MaterialTheme.typography.displayLarge) },
                    title = stringResource(Res.string.onboarding_cookies_title),
                    description = stringResource(Res.string.onboarding_cookies_text),
                    primaryButtonText = stringResource(Res.string.onboarding_cookies_allow_all),
                    onPrimaryClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    secondaryButtonText = stringResource(Res.string.onboarding_cookies_necessary),
                    onSecondaryClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                )
                2 -> OnboardingPage(
                    image = { Text("📍", style = MaterialTheme.typography.displayLarge) },
                    title = stringResource(Res.string.onboarding_location_title),
                    description = stringResource(Res.string.onboarding_location_text),
                    primaryButtonText = stringResource(Res.string.onboarding_location_share),
                    onPrimaryClick = requestLocation,
                    secondaryButtonText = stringResource(Res.string.onboarding_skip),
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
