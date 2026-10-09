package com.example.tableo.ui.staff.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import org.koin.compose.viewmodel.koinViewModel
import com.example.tableo.resources.Res
import com.example.tableo.resources.team_title
import com.example.tableo.resources.common_back
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.team_empty
import org.jetbrains.compose.resources.stringResource

/** Team eines Restaurants mit Profilbild, Rolle und wer gerade im Dienst ist. Antippen zeigt Details, Rolle und Entfernen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTeamScreen(
    onBackClick: () -> Unit = {},
    staffTeamViewModel: StaffTeamViewModel = koinViewModel()
) {
    val uiState by staffTeamViewModel.uiState.collectAsState()
    val actionError by staffTeamViewModel.actionError.collectAsState()

    // nur die ID merken, damit das Blatt neue Rollen direkt aus der Liste zeigt
    var selectedId by remember { mutableStateOf<String?>(null) }
    val staff = (uiState as? StaffTeamUiState.Success)?.staff.orEmpty()
    val selected = staff.firstOrNull { it.id == selectedId }
    val canManage = staffTeamViewModel.canManage(staff)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.team_title, staffTeamViewModel.restaurantName),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            RestaurantIdCard(staffTeamViewModel.restaurantId)

            when (val state = uiState) {
                is StaffTeamUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is StaffTeamUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                    }
                }

                is StaffTeamUiState.Success -> {
                    if (state.staff.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(Res.string.team_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.staff, key = { it.id }) { profile ->
                                StaffRow(
                                    profile = profile,
                                    isMe = profile.userId == staffTeamViewModel.currentUserId,
                                    onClick = { selectedId = profile.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selected != null) {
        StaffDetailSheet(
            profile = selected,
            isMe = selected.userId == staffTeamViewModel.currentUserId,
            canManage = canManage,
            error = actionError,
            onRoleSelected = { role -> staffTeamViewModel.updateRole(selected, role) },
            onRemove = { staffTeamViewModel.removeStaff(selected) { selectedId = null } },
            onDismiss = {
                selectedId = null
                staffTeamViewModel.clearActionError()
            }
        )
    }
}

@Preview
@Composable
private fun StaffTeamScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    StaffTeamScreen(
        staffTeamViewModel = StaffTeamViewModel(savedStateHandle, StaffRepositoryMock(), AuthRepositoryMock())
    )
}
