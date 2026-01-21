package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto

@Composable
fun ScheduleList(data: List<ScheduleByDateDto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Отступ под нижним меню
    ) {
        items(data) { day ->
            // Заголовок дня недели
            Text(
                text = "${day.lessonDate} (${day.weekday})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            if (day.lessons.isEmpty()) {
                Text(
                    "Занятий нет",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                day.lessons.forEach { lesson ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            // Левая колонка Время и номер пары
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(60.dp)
                            ) {
                                Text(
                                    text = lesson.lessonNumber.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lesson.time.replace(" - ", "\n"), // Перенос строки
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Правая колонка Детали
                            Column {
                                lesson.groupParts.forEach { (part, info) ->
                                    if (info != null) {
                                        // Название предмета
                                        Text(
                                            text = info.subject,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Преподаватель
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = info.teacher,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Аудитория и Корпус
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = getBuildingColor(info.building)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${info.classroom} (${info.building})",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Вспомогательная функция для цветов корпусов
fun getBuildingColor(buildingName: String): Color {
    return when {
        buildingName.contains("Главный", true) -> Color(0xFF1976D2) // Синий
        buildingName.contains("Лабораторный", true) -> Color(0xFF388E3C) // Зеленый
        buildingName.contains("Спортивный", true) -> Color(0xFFD32F2F) // Красный
        else -> Color.Gray
    }
}