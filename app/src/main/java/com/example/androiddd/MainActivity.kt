package com.example.androiddd

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton // <-- Добавлено
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androiddd.data.models.Lesson // <-- Добавлено
import com.example.androiddd.data.repository.ScheduleRepository // <-- Добавлено (если уже создан)
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var daysContainer: LinearLayout
    private lateinit var scheduleContainer: LinearLayout
    private lateinit var prevWeekBtn: Button
    private lateinit var nextWeekBtn: Button
    private lateinit var weekRangeText: TextView
    private lateinit var weekTypeText: TextView
    private var selectedDayButton: LinearLayout? = null
    private var currentWeekOffset = 0
    private var todayDayName: String = "Понедельник"

    private val calendarDays = mapOf(
        Calendar.MONDAY to "Понедельник",
        Calendar.TUESDAY to "Вторник",
        Calendar.WEDNESDAY to "Среда",
        Calendar.THURSDAY to "Четверг",
        Calendar.FRIDAY to "Пятница",
        Calendar.SATURDAY to "Суббота",
        Calendar.SUNDAY to "Воскресенье"
    )

    private val scheduleNumerator = mapOf(
        "Понедельник" to listOf(
            Lesson("15:40-17:00", "Распределенные задачи и алгоритмы", "Ауд. 129", "Приходько Т.А.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("17:10-18:30", "Нейросетевые и нечеткие модели", "Ауд. А301б", "Городецкий Э.Р.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("18:40-20:00", "Модели интеллектуальных систем", "Ауд. А301б", "Городецкий Э.Р.", "Лаб", 0xFFFF5722.toInt())
        ),
        "Вторник" to listOf(
            Lesson("Весь день", "КСРС", "Самостоятельная работа", "Самообучение", "КСРС", 0xFF9C27B0.toInt())
        ),
        "Среда" to listOf(
            Lesson("12:40-14:00", "Основы компьютерной графики", "Ауд. 103а", "Гаркуша О.В.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("14:10-15:30", "Информационная безопасность", "Ауд. 131", "Шиян В.И.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("15:40-17:00", "Информационная безопасность", "Ауд. 128", "Шиян В.И.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("17:10-18:30", "Программирование для мобильных платформ", "Ауд. 128", "Приходько Т.А.", "Лекция", 0xFF2196F3.toInt()),
        ),
        "Четверг" to listOf(
            Lesson("14:10-15:30", "Основы компьютерной графики", "Ауд. A305", "Гаркуша О.В.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("15:40-17:00", "Тестирование и отладка ПО", "Ауд. 147", "Городецкий Э.Р.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("17:10-18:30", "Модели интеллектуальных систем", "Ауд. 131", "Костенко К.И.", "Лекция",0xFF2196F3.toInt()),
            Lesson("18:40-20:00", "Программирование для мобильных платформ","Ауд. 102а","Яхонтов А.А.","Лаб", 0xFFFF5722.toInt())
        ),
        "Пятница" to listOf(
            Lesson("14:10-15:30", "Тестирование и отладка ПО","Ауд. A305","Городецкий Э.Р.","Лекция",0xFF2196F3.toInt()),
            Lesson("15:40-17:00", "Нейросетевые и нечеткие модели","Ауд. 129", "Руденко О.В.","Лекция",0xFF2196F3.toInt()),
            Lesson("17:10-18:30", "Основы военной подготовки", "Ауд. 100C", "Крылов Д.С.","П/З",0xFF2196F3.toInt()),
        ),
        "Суббота" to listOf(
            Lesson("8:00-9:20", "Бэкенд разработка", "Ауд. 128", "Кесян Г.Р.","Лекция",0xFF2196F3.toInt()),
            Lesson("9:30-10:50", "Распределенные задачи и алгоритмы", "Ауд. 102a", "Яхонтов А.А.","Лаб", 0xFFFF5722.toInt()),
            Lesson("11:10-12:30", "Алгоритмы цифровой обработки мультимедиа", "Ауд. 101", "Крамаренко А.А.","Лаб", 0xFFFF5722.toInt()),
            Lesson("12:40-14:00", "Основы военной подготовки", "Ауд. 131", "Крылов Д.С.", "Лекция", 0xFF2196F3.toInt()),
        ),
        "Воскресенье" to emptyList()
    )

    private val scheduleDenominator = mapOf(
        "Понедельник" to listOf(
            Lesson("15:40-17:00", "Распределенные задачи и алгоритмы", "Ауд. 129", "Приходько Т.А.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("17:10-18:30", "Нейросетевые и нечеткие модели", "Ауд. А301б", "Городецкий Э.Р.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("18:40-20:00", "Модели интеллектуальных систем", "Ауд. А301б", "Городецкий Э.Р.", "Лаб", 0xFFFF5722.toInt())
        ),
        "Вторник" to listOf(
            Lesson("Весь день", "КСРС", "Самостоятельная работа", "Самообучение", "КСРС", 0xFF9C27B0.toInt())
        ),
        "Среда" to listOf(
            Lesson("15:40-17:00", "Информационная безопасность", "Ауд. 128", "Шиян В.И.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("17:10-18:30", "Программирование для мобильных платформ", "Ауд. 128", "Приходько Т.А.", "Лекция", 0xFF2196F3.toInt()),
        ),
        "Четверг" to listOf(
            Lesson("14:10-15:30", "Алгоритмы цифровой обработки мультимедиа", "Ауд. 128", "Крамаренко А.А.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("15:40-17:00", "Тестирование и отладка ПО", "Ауд. 147", "Городецкий Э.Р.", "Лаб", 0xFFFF5722.toInt()),
            Lesson("17:10-18:30", "Модели интеллектуальных систем", "Ауд. 131", "Костенко К.И.", "Лекция",0xFF2196F3.toInt()),
            Lesson("18:40-20:00", "Программирование для мобильных платформ","Ауд. 102а","Яхонтов А.А.","Лаб", 0xFFFF5722.toInt())
        ),
        "Пятница" to listOf(
            Lesson("14:10-15:30", "Информационная безопасность", "Ауд. A305", "Шиян В.И.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("15:40-17:00", "Нейросетевые и нечеткие модели","Ауд. 129", "Руденко О.В.","Лекция",0xFF2196F3.toInt()),
            Lesson("17:10-18:30", "Основы военной подготовки", "Ауд. 100C", "Крылов Д.С.","П/З",0xFF2196F3.toInt()),
        ),
        "Суббота" to listOf(
            Lesson("9:30-10:50", "Распределенные задачи и алгоритмы", "Ауд. 102a", "Яхонтов А.А.","Лаб", 0xFFFF5722.toInt()),
            Lesson("11:10-12:30", "Бэкенд разработка", "Ауд. 102", "Кесян Г.Р.","Лаб", 0xFFFF5722.toInt()),
            Lesson("12:40-14:00", "Основы военной подготовки", "Ауд. 131", "Крылов Д.С.", "Лекция", 0xFF2196F3.toInt()),
            Lesson("14:10-15:30", "Алгоритмы цифровой обработки мультимедиа", "Ауд. 128", "Крамаренко А.А.","Лаб", 0xFFFF5722.toInt()),
        ),
        "Воскресенье" to emptyList()
    )

    companion object {
        private const val LESSON_DETAIL_REQUEST_CODE = 1001
        // Новый ключ для передачи дня недели
        const val EXTRA_DAY_NAME = "day_name"
        // Новый ключ для передачи признака новой пары
        const val EXTRA_IS_NEW_LESSON = "is_new_lesson"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        daysContainer = findViewById(R.id.daysContainer)
        scheduleContainer = findViewById(R.id.scheduleContainer)
        prevWeekBtn = findViewById(R.id.prevWeekBtn)
        nextWeekBtn = findViewById(R.id.nextWeekBtn)
        weekRangeText = findViewById(R.id.weekRangeText)
        weekTypeText = findViewById(R.id.weekTypeText)

        determineCurrentDay()
        setupWeekNavigation()
        setupDayButtons()
        showTodaySchedule()
    }
    private fun determineCurrentDay() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        todayDayName = calendarDays[currentDayOfWeek] ?: "Понедельник"
    }

    private fun setupWeekNavigation() {
        prevWeekBtn.setOnClickListener {
            currentWeekOffset--
            updateWeekDisplay()
            refreshDayButtons()
        }
        nextWeekBtn.setOnClickListener {
            currentWeekOffset++
            updateWeekDisplay()
            refreshDayButtons()
        }
        weekRangeText.setOnClickListener {
            currentWeekOffset = 0
            updateWeekDisplay()
            refreshDayButtons()
            showTodaySchedule()
        }
        updateWeekDisplay()
    }

    private fun updateWeekDisplay() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        val startMonth = getRussianMonth(calendar.get(Calendar.MONTH))

        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val endDay = calendar.get(Calendar.DAY_OF_MONTH)
        val endMonth = getRussianMonth(calendar.get(Calendar.MONTH))

        val weekText = if (startMonth == endMonth) {
            "$startDay - $endDay $startMonth"
        } else {
            "$startDay $startMonth - $endDay $endMonth"
        }
        weekRangeText.text = weekText

        val isNumerator = determineWeekType()
        val weekType = if (isNumerator) "(числитель)" else "(знаменатель)"
        weekTypeText.text = weekType

        if (currentWeekOffset == 0) {
            weekRangeText.setTextColor(0xFFFFFF00.toInt())
            weekTypeText.setTextColor(0xFFFFFF00.toInt())
        } else {
            weekRangeText.setTextColor(0xFFFFFFFF.toInt())
            weekTypeText.setTextColor(0xFFE3F2FD.toInt())
        }
    }

    private fun determineWeekType(): Boolean {
        val academicYearStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val diffInMillis = calendar.timeInMillis - academicYearStart.timeInMillis
        val diffInWeeks = (diffInMillis / (1000 * 60 * 60 * 24 * 7)).toInt()

        return (diffInWeeks % 2 == 0)
    }

    private fun getRussianMonth(month: Int): String {
        val months = listOf("янв", "фев", "мар", "апр", "мая", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек")
        return months[month]
    }

    private fun refreshDayButtons() {
        daysContainer.removeAllViews()
        selectedDayButton = null
        setupDayButtons()
    }
    private fun setupDayButtons() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val dayNames = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
        val shortNames = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")

        for (i in 0 until 7) {
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val fullName = dayNames[i]
            val dayButton = LayoutInflater.from(this).inflate(
                R.layout.layout_day_button,
                daysContainer,
                false
            ) as LinearLayout

            dayButton.findViewById<TextView>(R.id.dayShortName).text = shortNames[i]
            dayButton.findViewById<TextView>(R.id.dayDate).text = dayNumber.toString()

            dayButton.setOnClickListener {
                selectDayButton(dayButton)
                showDaySchedule(fullName)
            }
            daysContainer.addView(dayButton)

            if (currentWeekOffset == 0 && fullName == todayDayName) {
                selectDayButton(dayButton)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        if (selectedDayButton == null && daysContainer.childCount > 0) {
            selectDayButton(daysContainer.getChildAt(0) as LinearLayout)
            showDaySchedule(dayNames[0])
        }
    }

    private fun selectDayButton(button: LinearLayout) {
        selectedDayButton?.setBackgroundResource(R.drawable.day_button_background)
        button.setBackgroundResource(R.drawable.day_button_selected)
        selectedDayButton = button
    }

    private fun showTodaySchedule() {
        showDaySchedule(todayDayName)
    }

    private fun refreshSchedule() {
        val currentDay = getCurrentSelectedDay()
        showDaySchedule(currentDay)
    }

    private fun getCurrentSelectedDay(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val dayNames = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
        for (i in 0 until daysContainer.childCount) {
            if (daysContainer.getChildAt(i) == selectedDayButton) {
                return dayNames[i]
            }
        }
        return "Понедельник"
    }

    private fun showDaySchedule(dayName: String) {
        scheduleContainer.removeAllViews()
        val dayCard = LayoutInflater.from(this).inflate(
            R.layout.layout_day_card,
            scheduleContainer,
            false
        )

        val dayTitle = dayCard.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = dayName
        dayTitle.setTextColor(0xFF333333.toInt())

        // Находим кнопку добавления
        val addLessonButton = dayCard.findViewById<Button>(R.id.addLessonButton)
        addLessonButton.setOnClickListener {
            showAddLessonDialog(dayName) // <-- Новый метод
        }

        val lessonsContainer = dayCard.findViewById<LinearLayout>(R.id.lessonsContainer)
        val isNumeratorWeek = determineWeekType()
        val schedule = if (isNumeratorWeek) scheduleNumerator else scheduleDenominator
        val lessons = schedule[dayName] ?: emptyList()

        // Собираем все пары: из жёсткого расписания и добавленные пользователем
        val allLessons = mutableListOf<Lesson>()
        allLessons.addAll(lessons) // Добавляем пары из жёсткого расписания

        // Загружаем добавленные пользователем пары
        // Используем ключ вида "user_added_lessons_ДеньНедели"
        val userAddedLessons = getUserAddedLessonsForDay(dayName)
        allLessons.addAll(userAddedLessons)

        // Сортируем пары по времени (для корректного отображения)
        // Предположим, что время в формате "HH:mm-HH:mm"
        allLessons.sortBy { lesson ->
            // Извлекаем время начала из строки "HH:mm-HH:mm"
            lesson.time.substringBefore("-").replace(":", "").toIntOrNull() ?: Int.MAX_VALUE
        }

        if (allLessons.isEmpty()) {
            val message = when (dayName) {
                "Воскресенье" -> "🎉 Воскресенье - выходной день!"
                else -> "📚 На этой неделе пар нет"
            }
            val emptyText = TextView(this).apply {
                text = message
                textSize = 18f
                setTextColor(0xFF666666.toInt())
                gravity = android.view.Gravity.CENTER
                setPadding(0, 60, 0, 60)
            }
            lessonsContainer.addView(emptyText)
        } else {
            allLessons.forEach { lesson ->
                val lessonView = LayoutInflater.from(this).inflate(
                    R.layout.layout_lesson_item,
                    lessonsContainer,
                    false
                )

                // Используем сохраненные данные
                // Используем ключ name_time для получения данных
                val savedName = getSavedLessonData(lesson.name, lesson.time, "name", lesson.name)
                val savedTime = getSavedLessonData(lesson.name, lesson.time, "time", lesson.time)
                val savedTeacher = getSavedLessonData(lesson.name, lesson.time, "teacher", lesson.teacher)
                val savedRoom = getSavedLessonData(lesson.name, lesson.time, "room", lesson.room)
                val savedType = getSavedLessonData(lesson.name, lesson.time, "type", lesson.type)
                val savedTypeColor = getSavedLessonColor(lesson.name, lesson.time, "type_color", lesson.typeColor)

                lessonView.findViewById<TextView>(R.id.lessonTime).text = savedTime
                lessonView.findViewById<TextView>(R.id.lessonName).text = savedName
                lessonView.findViewById<TextView>(R.id.lessonRoom).text = savedRoom
                lessonView.findViewById<TextView>(R.id.lessonTeacher).text = savedTeacher
                val typeView = lessonView.findViewById<TextView>(R.id.lessonType)
                typeView.text = savedType
                typeView.setBackgroundColor(savedTypeColor)
                typeView.setTextColor(0xFFFFFFFF.toInt())

                lessonView.setOnClickListener {
                    openLessonDetails(lesson)
                }
                lessonsContainer.addView(lessonView)
            }
        }
        scheduleContainer.addView(dayCard)
    }
    private fun showAddLessonDialog(dayName: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_lesson, null)
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val nameEditText = dialogView.findViewById<android.widget.EditText>(R.id.newLessonName)
        val timeEditText = dialogView.findViewById<android.widget.EditText>(R.id.newLessonTime)
        val teacherEditText = dialogView.findViewById<android.widget.EditText>(R.id.newLessonTeacher)
        val roomEditText = dialogView.findViewById<android.widget.EditText>(R.id.newLessonRoom)
        val typeSpinner = dialogView.findViewById<android.widget.Spinner>(R.id.newLessonTypeSpinner)

        // Установка значений по умолчанию
        nameEditText.setText("Новая дисциплина")
        timeEditText.setText("00:00-00:00") // Пользователь должен ввести
        teacherEditText.setText("Преподаватель")
        roomEditText.setText("Ауд.")

        // Настройка спиннера типов (используем те же типы, что и в LessonDetailActivity)
        val lessonTypes = listOf("Лекция", "П/З", "Лаб", "Семинар", "Консультация", "Доп занятие")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, lessonTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter
        typeSpinner.setSelection(0) // Выбираем "Лекция" по умолчанию

        val addButton = dialogView.findViewById<Button>(R.id.addLessonConfirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.addLessonCancelButton)

        addButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            val newTime = timeEditText.text.toString().trim()
            val newTeacher = teacherEditText.text.toString().trim()
            val newRoom = roomEditText.text.toString().trim()
            val newType = lessonTypes[typeSpinner.selectedItemPosition] // Получаем выбранный тип

            if (newName.isNotBlank() && newTime.isNotBlank()) {
                // Сохраняем новую пару
                addNewLesson(dayName, newTime, newName, newTeacher, newRoom, newType)
                dialog.dismiss()
            } else {
                // Можно показать Toast, если поля не заполнены
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // НОВЫЙ МЕТОД: Сохранить новую пару
    private fun addNewLesson(dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String) {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Генерируем уникальное имя для новой пары: день_время (или просто время, если день уникален)
        // Лучше использовать день_время как уникальный ключ для сохранения
        val uniqueName = "${dayName}_${lessonTime}"

        // Сохраняем данные новой пары под уникальным ключом (имя_время)
        editor.putString("${uniqueName}_name", lessonName)
        editor.putString("${uniqueName}_time", lessonTime)
        editor.putString("${uniqueName}_teacher", lessonTeacher)
        editor.putString("${uniqueName}_room", lessonRoom)
        editor.putString("${uniqueName}_type", lessonType)
        editor.putInt("${uniqueName}_type_color", 0xFF2196F3.toInt()) // Цвет по умолчанию

        // Добавляем новую пару в список для дня
        addToUserAddedLessonsForDay(editor, dayName, lessonTime, lessonName, lessonTeacher, lessonRoom, lessonType, 0xFF2196F3.toInt())

        editor.apply()
        refreshCurrentSchedule()
    }

    // Метод для добавления новой пары в JSON-список для дня
    private fun addToUserAddedLessonsForDay(editor: android.content.SharedPreferences.Editor, dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String, lessonTypeColor: Int) {
        val key = "user_added_lessons_$dayName"
        val currentJsonString = getSharedPreferences("lesson_data", MODE_PRIVATE).getString(key, "[]") ?: "[]"
        val jsonArray = try {
            org.json.JSONArray(currentJsonString)
        } catch (e: Exception) {
            org.json.JSONArray() // Если строка повреждена, начинаем с пустого массива
        }

        val lessonJson = org.json.JSONObject().apply {
            put("name", lessonName)
            put("time", lessonTime)
            put("teacher", lessonTeacher)
            put("room", lessonRoom)
            put("type", lessonType)
            put("typeColor", lessonTypeColor)
        }
        jsonArray.put(lessonJson)

        editor.putString(key, jsonArray.toString())
    }

    private fun openLessonDetails(lesson: Lesson) {
        // Берем сохраненные данные
        // Используем ключ name_time для получения данных
        val savedName = getSavedLessonData(lesson.name, lesson.time, "name", lesson.name)
        val savedTime = getSavedLessonData(lesson.name, lesson.time, "time", lesson.time)
        val savedTeacher = getSavedLessonData(lesson.name, lesson.time, "teacher", lesson.teacher)
        val savedRoom = getSavedLessonData(lesson.name, lesson.time, "room", lesson.room)
        val savedType = getSavedLessonData(lesson.name, lesson.time, "type", lesson.type)
        val savedTypeColor = getSavedLessonColor(lesson.name, lesson.time, "type_color", lesson.typeColor)

        val intent = Intent(this, LessonDetailActivity::class.java).apply {
            putExtra("LESSON_NAME", savedName)
            putExtra("LESSON_TIME", savedTime)
            putExtra("LESSON_TEACHER", savedTeacher)
            putExtra("LESSON_ROOM", savedRoom)
            putExtra("LESSON_TYPE", savedType)
            putExtra("LESSON_TYPE_COLOR", savedTypeColor)
            putExtra("ORIGINAL_LESSON_NAME", lesson.name) // <-- Важно: оригинальное имя
            putExtra("ORIGINAL_LESSON_TIME", lesson.time) // <-- Важно: оригинальное время
            putExtra(EXTRA_IS_NEW_LESSON, false) // <-- Передаём признак редактирования существующей
        }
        startActivityForResult(intent, LESSON_DETAIL_REQUEST_CODE)
    }

    // ... (остальные методы без изменений до onActivityResult)

    // НОВЫЕ МЕТОДЫ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЬСКИМИ ПАРАМИ

    // Метод для получения данных СУЩЕСТВУЮЩЕЙ пары (используется для редактирования и отображения)
    // Обновлен: принимает name и time
    private fun getSavedLessonData(originalName: String, originalTime: String, field: String, defaultValue: String): String {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        val key = "${originalName}_${originalTime}_$field" // <-- Ключ: имя_время_поле
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    // Метод для получения цвета СУЩЕСТВУЮЩЕЙ пары
    // Обновлен: принимает name и time
    private fun getSavedLessonColor(originalName: String, originalTime: String, field: String, defaultValue: Int): Int {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        val key = "${originalName}_${originalTime}_$field" // <-- Ключ: имя_время_поле
        return sharedPref.getInt(key, defaultValue)
    }

    // Метод для получения списка добавленных пользователем пар для конкретного дня
    private fun getUserAddedLessonsForDay(dayName: String): List<Lesson> {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        val key = "user_added_lessons_$dayName" // <-- Ключ для списка пар дня
        val jsonString = sharedPref.getString(key, "[]") ?: "[]"
        val lessons = mutableListOf<Lesson>()

        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val lessonJson = jsonArray.getJSONObject(i)
                val lesson = Lesson(
                    time = lessonJson.getString("time"),
                    name = lessonJson.getString("name"),
                    teacher = lessonJson.getString("teacher"),
                    room = lessonJson.getString("room"),
                    type = lessonJson.getString("type"),
                    typeColor = lessonJson.getInt("typeColor")
                )
                lessons.add(lesson)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // В случае ошибки возвращаем пустой список
        }

        return lessons
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LESSON_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // Этот блок теперь ОБНОВЛЯЕТ ТОЛЬКО СУЩЕСТВУЮЩИЕ пары
            val updatedName = data?.getStringExtra("UPDATED_LESSON_NAME")
            val updatedTime = data?.getStringExtra("UPDATED_LESSON_TIME")
            val updatedTeacher = data?.getStringExtra("UPDATED_LESSON_TEACHER")
            val updatedRoom = data?.getStringExtra("UPDATED_LESSON_ROOM")
            val updatedType = data?.getStringExtra("UPDATED_LESSON_TYPE")
            val updatedTypeColor = data?.getIntExtra("UPDATED_LESSON_TYPE_COLOR", 0xFF2196F3.toInt())
            val originalName = data?.getStringExtra("ORIGINAL_LESSON_NAME")
            val originalTime = data?.getStringExtra("ORIGINAL_LESSON_TIME") // <-- Добавлено

            // ВАЖНО: Проверяем originalName и originalTime, чтобы убедиться, что это редактирование, а не добавление
            if (updatedName != null && originalName != null && originalTime != null) {
                // Сохраняем все данные под ОРИГИНАЛЬНЫМ ключом (originalName_originalTime)
                val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("${originalName}_${originalTime}_name", updatedName)
                    putString("${originalName}_${originalTime}_time", updatedTime ?: "")
                    putString("${originalName}_${originalTime}_teacher", updatedTeacher ?: "")
                    putString("${originalName}_${originalTime}_room", updatedRoom ?: "")
                    putString("${originalName}_${originalTime}_type", updatedType ?: "")
                    putInt("${originalName}_${originalTime}_type_color", updatedTypeColor ?: 0xFF2196F3.toInt())
                    apply()
                }
                refreshCurrentSchedule()
            }
        }
    }

    // Метод для сохранения новой пары в SharedPreferences
    private fun saveNewLessonForDay(editor: android.content.SharedPreferences.Editor, dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String, lessonTypeColor: Int) {
        val key = "user_added_lessons_$dayName"
        val currentJsonString = getSharedPreferences("lesson_data", MODE_PRIVATE).getString(key, "[]") ?: "[]"
        val jsonArray = try {
            org.json.JSONArray(currentJsonString)
        } catch (e: Exception) {
            org.json.JSONArray() // Если строка повреждена, начинаем с пустого массива
        }

        val lessonJson = org.json.JSONObject().apply {
            put("name", lessonName)
            put("time", lessonTime) // Используем переданное время как часть ключа
            put("teacher", lessonTeacher)
            put("room", lessonRoom)
            put("type", lessonType)
            put("typeColor", lessonTypeColor)
        }
        jsonArray.put(lessonJson)

        editor.putString(key, jsonArray.toString())
    }
    private fun refreshCurrentSchedule() {
        val currentDay = getCurrentSelectedDay()
        showDaySchedule(currentDay)
    }
}