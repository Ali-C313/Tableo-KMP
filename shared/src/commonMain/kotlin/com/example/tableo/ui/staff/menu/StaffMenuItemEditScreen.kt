package com.example.tableo.ui.staff.menu

import com.example.tableo.ui.theme.AppShape
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.rememberImagePicker
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import coil3.compose.AsyncImage
import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.repository.mock.MenuRepositoryMock
import com.example.tableo.data.repository.mock.StorageRepositoryMock
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.components.ImagePlaceholder
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.input.ImeAction
import com.example.tableo.resources.Res
import com.example.tableo.resources.staff_menu_new_item
import com.example.tableo.resources.staff_menu_edit_item
import com.example.tableo.resources.common_back
import com.example.tableo.resources.staff_menu_selected_photo
import com.example.tableo.resources.staff_menu_current_photo
import com.example.tableo.resources.staff_menu_change_photo
import com.example.tableo.resources.staff_menu_pick_photo
import com.example.tableo.resources.staff_menu_name
import com.example.tableo.resources.staff_menu_description
import com.example.tableo.resources.staff_menu_price
import com.example.tableo.resources.staff_menu_category
import com.example.tableo.resources.staff_menu_choose_category
import com.example.tableo.resources.staff_menu_category_other
import com.example.tableo.resources.staff_menu_new_category
import com.example.tableo.resources.staff_menu_available
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.staff_menu_saving
import com.example.tableo.resources.common_save
import org.jetbrains.compose.resources.stringResource

// interner Merker für eigene Kategorie, angezeigt wird der übersetzte Text
private const val OTHER_CATEGORY = "Andere"

/**
 * Formular zum Anlegen oder Bearbeiten eines Speisekarten-Artikels. Restaurant-ID und
 * die zu bearbeitende menuItemId kommen über die Route direkt ins ViewModel.
 *
 * @param onSaved Wird nach erfolgreichem Speichern aufgerufen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMenuItemEditScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit = {},
    staffMenuItemEditViewModel: StaffMenuItemEditViewModel = koinViewModel()
) {
    val uiState by staffMenuItemEditViewModel.uiState.collectAsState()
    val prefill by staffMenuItemEditViewModel.prefill.collectAsState()
    val categories by staffMenuItemEditViewModel.categories.collectAsState()
    val menuItemId = staffMenuItemEditViewModel.menuItemId

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    // "4,50" und "4.50" sind beide gültig
    val parsedPrice = FormatHelper.parsePrice(price)
    var categoryOption by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var existingImageUrl by remember { mutableStateOf("") }
    // Bild liegt bis zum Speichern nur im Speicher, hochgeladen wird erst beim Speichern
    var pickedImage by remember { mutableStateOf<ByteArray?>(null) }
    var isAvailable by remember { mutableStateOf(true) }

    val pickImage = rememberImagePicker { bytes -> if (bytes != null) pickedImage = bytes }

    // sobald der zu bearbeitende Artikel geladen ist, Formularfelder damit befüllen
    LaunchedEffect(prefill, categories) {
        prefill?.let { item ->
            name = item.name
            description = item.description
            price = FormatHelper.decimals(item.price, 2)
            existingImageUrl = item.imageUrl
            isAvailable = item.available
            categoryOption = if (categories.contains(item.category)) item.category else OTHER_CATEGORY
            customCategory = if (categories.contains(item.category)) "" else item.category
        }
    }

    // ohne Vorbelegung eine sinnvolle Standardauswahl setzen, sobald die Kategorien geladen sind
    LaunchedEffect(categories) {
        if (categoryOption.isBlank()) {
            categoryOption = categories.firstOrNull() ?: OTHER_CATEGORY
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is StaffMenuItemEditUiState.Saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (menuItemId == null) stringResource(Res.string.staff_menu_new_item) else stringResource(Res.string.staff_menu_edit_item)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(AppShape.Small)
            ) {
                when {
                    pickedImage != null -> AsyncImage(
                        model = pickedImage,
                        contentDescription = stringResource(Res.string.staff_menu_selected_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    existingImageUrl.isNotBlank() -> AsyncImage(
                        model = existingImageUrl,
                        contentDescription = stringResource(Res.string.staff_menu_current_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> ImagePlaceholder()
                }
            }
            OutlinedButton(
                onClick = pickImage,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    if (pickedImage != null || existingImageUrl.isNotBlank()) stringResource(Res.string.staff_menu_change_photo)
                    else stringResource(Res.string.staff_menu_pick_photo)
                )
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text(stringResource(Res.string.staff_menu_name)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text(stringResource(Res.string.staff_menu_description)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            )
            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text(stringResource(Res.string.staff_menu_price)) },
                isError = price.isNotBlank() && parsedPrice == null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            )

            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(stringResource(Res.string.staff_menu_category), style = MaterialTheme.typography.bodyMedium)
                DropdownPicker(
                    selected = categoryOption.ifBlank { null },
                    options = categories + OTHER_CATEGORY,
                    onSelected = { categoryOption = it },
                    placeholder = stringResource(Res.string.staff_menu_choose_category),
                    fillWidth = true,
                    optionLabel = { if (it == OTHER_CATEGORY) stringResource(Res.string.staff_menu_category_other) else it }
                )
                if (categoryOption == OTHER_CATEGORY) {
                    OutlinedTextField(
                        value = customCategory, onValueChange = { customCategory = it },
                        label = { Text(stringResource(Res.string.staff_menu_new_category)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(stringResource(Res.string.staff_menu_available), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
            }

            if (uiState is StaffMenuItemEditUiState.Error) {
                Text(
                    text = stringResource(Res.string.error_with_message, (uiState as StaffMenuItemEditUiState.Error).message),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val finalCategory = if (categoryOption == OTHER_CATEGORY) customCategory else categoryOption
                    staffMenuItemEditViewModel.save(
                        menuItem = MenuItem(
                            id = menuItemId ?: "",
                            restaurantId = staffMenuItemEditViewModel.restaurantId,
                            name = name,
                            description = description,
                            price = parsedPrice ?: 0.0,
                            category = finalCategory,
                            imageUrl = existingImageUrl,
                            available = isAvailable
                        ),
                        newImageBytes = pickedImage
                    )
                },
                enabled = name.isNotBlank() && parsedPrice != null && uiState !is StaffMenuItemEditUiState.Saving,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                Text(
                    if (uiState is StaffMenuItemEditUiState.Saving) stringResource(Res.string.staff_menu_saving)
                    else stringResource(Res.string.common_save)
                )
            }
        }
    }
}

@Preview
@Composable
private fun StaffMenuItemEditScreenPreview() {
    // menuItemId "1" -> Bearbeiten-Modus mit dem ersten Beispiel-Artikel aus MenuRepositoryMock
    val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "1", "menuItemId" to "1"))
    StaffMenuItemEditScreen(
        onSaved = {},
        staffMenuItemEditViewModel = StaffMenuItemEditViewModel(
            savedStateHandle, MenuRepositoryMock(), StorageRepositoryMock()
        )
    )
}