package com.example.collegeschedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.StudentGroupDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelector(
    groups: List<StudentGroupDto>,
    selectedGroup: String,
    onGroupSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Текст, который вводит пользователь
    var searchText by remember { mutableStateOf(selectedGroup) }

    // Фильтруем список групп на лету: если ввели текст, показываем только совпадения
    val filteredGroups = remember(searchText, groups) {
        if (searchText.isBlank()) groups
        else groups.filter { it.groupName.contains(searchText, ignoreCase = true) }
    }

    // Обновляем текст, если извне пришла новая выбранная группа
    LaunchedEffect(selectedGroup) {
        if (selectedGroup.isNotEmpty()) {
            searchText = selectedGroup
        }
    }

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    expanded = true // Открываем список при вводе
                },
                label = { Text("Выберите группу") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Привязываем меню к этому полю
            )

            // Само выпадающее меню
            if (filteredGroups.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.groupName) },
                            onClick = {
                                searchText = group.groupName
                                onGroupSelected(group.groupName) // Сообщаем наверх о выборе
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}