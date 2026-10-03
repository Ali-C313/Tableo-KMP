package com.example.tableo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tableo.ui.AppStart
import com.example.tableo.ui.theme.TableoTheme

/** Einstieg für Android (MainActivity) und iOS (MainViewController). */
@Composable
fun App() {
    TableoTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            AppStart()
        }
    }
}
