package com.example.testgameandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestGameAndroidTheme()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestGameAndroidTheme() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RegistrationScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen() {
    // states
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Не указано") } // "Муж", "Жен", "Другое"
    val genders = listOf("Муж", "Жен", "Другое")

    val courses = listOf("1 курс","2 курс","3 курс","4 курс","5 курс","6 курс")
    var courseExpanded by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf(courses.first()) }

    var difficulty by remember { mutableStateOf(5f) } // 0..10

    // DatePicker state
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var dobLabel by remember { mutableStateOf("Не выбрана") }
    var pickedMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // show DatePickerDialog when requested
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = pickedMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    pickedMillis = millis
                    if (millis != null) {
                        val dt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        dobLabel = dt.format(dateFormatter)
                    } else {
                        dobLabel = "Не выбрана"
                    }
                    showDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // computed zodiac name from pickedMillis
    val zodiac: String = remember(pickedMillis) {
        pickedMillis?.let {
            val dt = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            getZodiac(dt.dayOfMonth, dt.monthValue)
        } ?: "Неизвестно"
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Регистрация игрока", style = MaterialTheme.typography.titleLarge)

        Text("ФИО")
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("Иванов Иван Иванович") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Пол")
        Column {
            genders.forEach { g ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (g == gender),
                            onClick = { gender = g },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(selected = (g == gender), onClick = { gender = g })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(g)
                }
            }
        }

        Text("Курс")
        ExposedDropdownMenuBox(
            expanded = courseExpanded,
            onExpandedChange = { courseExpanded = !courseExpanded }
        ) {
            TextField(
                value = selectedCourse,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }) {
                courses.forEach { c ->
                    DropdownMenuItem(text = { Text(c) }, onClick = {
                        selectedCourse = c
                        courseExpanded = false
                    })
                }
            }
        }

        Text("Уровень сложности: ${difficulty.toInt()}")
        Slider(
            value = difficulty,
            onValueChange = { difficulty = it },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Дата рождения: $dobLabel")
        Button(onClick = { showDatePicker = true }) {
            Text("Выбрать дату")
        }

        // Zodiac image (замени drawable имена ниже на свои ресурсы)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Знак зодиака: $zodiac", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            val drawableId = getZodiacDrawableRes(zodiac)
            if (drawableId != null) {
                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = zodiac,
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Output block
        val outputText = remember(fullName, gender, selectedCourse, difficulty, dobLabel, zodiac) {
            """
            ФИО: ${if (fullName.isBlank()) "Не указано" else fullName}
            Пол: $gender
            Курс: $selectedCourse
            Сложность: ${difficulty.toInt()}
            Дата рождения: $dobLabel
            Знак: $zodiac
            """.trimIndent()
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // кнопка просто показывает/обновляет текст ниже (у нас уже computed)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Показать данные")
        }

        Text(outputText, style = MaterialTheme.typography.bodyMedium)
    }
}

fun getZodiac(day: Int, month: Int): String {
    return when {
        (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен"
        (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец"
        (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы"
        (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак"
        (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев"
        (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева"
        (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы"
        (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион"
        (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Стрелец"
        (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Козерог"
        (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей"
        (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "Рыбы"
        else -> "Неизвестно"
    }
}

fun getZodiacDrawableRes(zodiac: String): Int? {
    return when (zodiac) {
        "Овен" -> R.drawable.zodiac_aries
        "Телец" -> R.drawable.zodiac_taurus
        "Близнецы" -> R.drawable.zodiac_gemini
        "Рак" -> R.drawable.zodiac_cancer
        "Лев" -> R.drawable.zodiac_leo
        "Дева" -> R.drawable.zodiac_virgo
        "Весы" -> R.drawable.zodiac_libra
        "Скорпион" -> R.drawable.zodiac_scorpio
        "Стрелец" -> R.drawable.zodiac_sagittarius
        "Козерог" -> R.drawable.zodiac_capricorn
        "Водолей" -> R.drawable.zodiac_aquarius
        "Рыбы" -> R.drawable.zodiac_pisces
        else -> R.drawable.ic_unknown
    }
}
