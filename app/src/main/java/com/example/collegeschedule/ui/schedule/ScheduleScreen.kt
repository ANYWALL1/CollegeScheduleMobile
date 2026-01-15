package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.dto.StudentGroupDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.components.GroupSelector
import com.example.collegeschedule.utils.getWeekDateRange
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen() {
    // Состояния данных
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var allGroups by remember { mutableStateOf<List<StudentGroupDto>>(emptyList()) }

    // Состояние выбора
    var selectedGroupName by remember { mutableStateOf("") }

    // Состояния интерфейса
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope() // Для запуска запросов по клику


    LaunchedEffect(Unit) {
        try {
            allGroups = RetrofitInstance.api.getGroups()
        } catch (e: Exception) {
            error = "Не удалось загрузить группы: ${e.message}"
        }
    }


    fun loadScheduleForGroup(group: String) {
        scope.launch {
            loading = true
            error = null
            val (start, end) = getWeekDateRange()
            try {
                schedule = RetrofitInstance.api.getSchedule(group, start, end)
            } catch (e: Exception) {
                error = e.message
                schedule = emptyList()
            } finally {
                loading = false
            }
        }
    }

    // ВЕРСТКА
    Column {

        GroupSelector(
            groups = allGroups,
            selectedGroup = selectedGroupName,
            onGroupSelected = { newGroup ->
                selectedGroupName = newGroup
                loadScheduleForGroup(newGroup) // Сразу грузим расписание при выборе
            }
        )

        // Контент снизу
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text("Ошибка: $error")
            schedule.isEmpty() && selectedGroupName.isNotEmpty() -> Text("Нет пар на эту неделю")
            schedule.isNotEmpty() -> ScheduleList(schedule)
            else -> Text("Выберите группу, чтобы увидеть расписание")
        }
    }
}