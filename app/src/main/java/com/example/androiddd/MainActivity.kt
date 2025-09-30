package com.example.androiddd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Intent
import java.util.*

data class Lesson(
    val time: String,
    val name: String,
    val room: String,
    val teacher: String,
    val type: String,
    val typeColor: Int
)
data class DayInfo(
    val fullName: String,
    val shortName: String,
    val dayNumber: Int,
    val month: String
)

// Обновленный макет кнопки дня:
// "ПН\n15 сент"

class MainActivity : AppCompatActivity() {

    private lateinit var daysContainer: LinearLayout
    private lateinit var scheduleContainer: LinearLayout
    private lateinit var prevWeekBtn: Button
    private lateinit var nextWeekBtn: Button
    private lateinit var weekRangeText: TextView
    private lateinit var weekTypeText: TextView
    private var selectedDayButton: LinearLayout? = null  // Было Button
    private var isNumeratorWeek = true
    private var todayDayName: String = "Понедельник" // Сохраняем сегодняшний день

    private var currentWeekOffset = 0

    // Соответствие дней недели Calendar дням
    private val calendarDays = mapOf(
        Calendar.MONDAY to "Понедельник",
        Calendar.TUESDAY to "Вторник",
        Calendar.WEDNESDAY to "Среда",
        Calendar.THURSDAY to "Четверг",
        Calendar.FRIDAY to "Пятница",
        Calendar.SATURDAY to "Суббота",
        Calendar.SUNDAY to "Воскресенье"
    )

    // ЧИСЛИТЕЛЬ - данные для понедельника
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

    // ЗНАМЕНАТЕЛЬ - такие же данные для понедельника (как ты сказал)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация ВСЕХ view сначала
        daysContainer = findViewById(R.id.daysContainer)
        scheduleContainer = findViewById(R.id.scheduleContainer)
        prevWeekBtn = findViewById(R.id.prevWeekBtn)
        nextWeekBtn = findViewById(R.id.nextWeekBtn)
        weekRangeText = findViewById(R.id.weekRangeText)
        weekTypeText = findViewById(R.id.weekTypeText)
        // Определяем сегодняшний день
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

        // Кнопка "Сегодня" для быстрого возврата
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

        // Находим понедельник текущей отображаемой недели
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        val startMonth = getRussianMonth(calendar.get(Calendar.MONTH))

        // Переходим к воскресенью
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val endDay = calendar.get(Calendar.DAY_OF_MONTH)
        val endMonth = getRussianMonth(calendar.get(Calendar.MONTH))

        // Форматируем текст диапазона дат
        val weekText = if (startMonth == endMonth) {
            "$startDay - $endDay $startMonth"
        } else {
            "$startDay $startMonth - $endDay $endMonth"
        }

        weekRangeText.text = weekText

        // Определяем тип недели (числитель/знаменатель)
        val isNumerator = determineWeekType()
        val weekType = if (isNumerator) "(числитель)" else "(знаменатель)"
        weekTypeText.text = weekType

        // Выделяем текст если это текущая неделя
        if (currentWeekOffset == 0) {
            weekRangeText.setTextColor(0xFFFFFF00.toInt()) // Желтый цвет для текущей недели
            weekTypeText.setTextColor(0xFFFFFF00.toInt())
        } else {
            weekRangeText.setTextColor(0xFFFFFFFF.toInt()) // Белый цвет для других недель
            weekTypeText.setTextColor(0xFFE3F2FD.toInt())
        }
    }
    private fun determineWeekType(): Boolean {
        // Фиксированная дата начала учебного года (1 сентября 2024)
        val academicYearStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Берем понедельник текущей отображаемой недели
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Вычисляем разницу в неделях от начала учебного года
        val diffInMillis = calendar.timeInMillis - academicYearStart.timeInMillis
        val diffInWeeks = (diffInMillis / (1000 * 60 * 60 * 24 * 7)).toInt()

        // 1 сентября 2024 была первая неделя (числитель)
        // Четные недели - числитель, нечетные - знаменатель
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

    // Обновленный метод getFormattedDateForDay
    private fun getFormattedDateForDay(dayName: String): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dayNames = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
        val russianMonths = listOf("января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря")

        val dayIndex = dayNames.indexOf(dayName)
        if (dayIndex >= 0) {
            calendar.add(Calendar.DAY_OF_MONTH, dayIndex)
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val month = russianMonths[calendar.get(Calendar.MONTH)]
            val year = calendar.get(Calendar.YEAR)

            return "$dayName, $dayNumber $month $year"
        }

        return dayName
    }

    // Определяем сегодняшний день и тип недели
// Определяем сегодняшний день и тип недели
    private fun determineCurrentDayAndWeek() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Определяем сегодняшний день
        todayDayName = calendarDays[currentDayOfWeek] ?: "Понедельник"

        // Фиксированная дата начала учебного года (1 сентября 2024)
        val academicYearStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Вычисляем разницу в неделях
        val diffInMillis = calendar.timeInMillis - academicYearStart.timeInMillis
        val diffInWeeks = (diffInMillis / (1000 * 60 * 60 * 24 * 7)).toInt()

        // 1 сентября 2024 была первая неделя (числитель)
        // Чередуем: четные недели - числитель, нечетные - знаменатель (или наоборот)
        // Поэкспериментируй с этой формулой
        isNumeratorWeek = (diffInWeeks % 2 == 0)

        // Альтернативная формула (попробуй обе):
        // isNumeratorWeek = (diffInWeeks % 2 == 1)
    }

    // В setupDayButtons():
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

            // Выделяем сегодняшний день только если это текущая неделя
            if (currentWeekOffset == 0 && fullName == todayDayName) {
                selectDayButton(dayButton)
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Если ничего не выделено, выделяем первый день
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

        // Заголовок дня - ТОЛЬКО название дня (без даты)
        val dayTitle = dayCard.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = dayName // Просто название дня
        dayTitle.setTextColor(0xFF333333.toInt())

        // Пары
        val lessonsContainer = dayCard.findViewById<LinearLayout>(R.id.lessonsContainer)
        val isNumeratorWeek = determineWeekType()
        val schedule = if (isNumeratorWeek) scheduleNumerator else scheduleDenominator
        val lessons = schedule[dayName] ?: emptyList()

        if (lessons.isEmpty()) {
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
        }  else {

            lessons.forEach { lesson ->
                val lessonView = LayoutInflater.from(this).inflate(
                    R.layout.layout_lesson_item,
                    lessonsContainer,
                    false
                )

                // Используем сохраненные данные
                val savedName = getSavedLessonData(lesson.name, "name", lesson.name)
                val savedTime = getSavedLessonData(lesson.name, "time", lesson.time)
                val savedTeacher = getSavedLessonData(lesson.name, "teacher", lesson.teacher)
                val savedRoom = getSavedLessonData(lesson.name, "room", lesson.room)
                val savedType = getSavedLessonData(lesson.name, "type", lesson.type)
                val savedTypeColor = getSavedLessonColor(lesson.name, "type_color", lesson.typeColor) // Используем новый метод

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
    // В классе MainActivity добавляем константу
    companion object {
        private const val LESSON_DETAIL_REQUEST_CODE = 1001
    }

    private fun openLessonDetails(lesson: Lesson) {
        // Берем сохраненные данные
        val savedName = getSavedLessonData(lesson.name, "name", lesson.name)
        val savedTime = getSavedLessonData(lesson.name, "time", lesson.time)
        val savedTeacher = getSavedLessonData(lesson.name, "teacher", lesson.teacher)
        val savedRoom = getSavedLessonData(lesson.name, "room", lesson.room)
        val savedType = getSavedLessonData(lesson.name, "type", lesson.type)
        val savedTypeColor = getSavedLessonColor(lesson.name, "type_color", lesson.typeColor)

        val intent = Intent(this, LessonDetailActivity::class.java).apply {
            putExtra("LESSON_NAME", savedName)
            putExtra("LESSON_TIME", savedTime)
            putExtra("LESSON_TEACHER", savedTeacher)
            putExtra("LESSON_ROOM", savedRoom)
            putExtra("LESSON_TYPE", savedType)
            putExtra("LESSON_TYPE_COLOR", savedTypeColor) // Передаем сохраненный цвет
            putExtra("ORIGINAL_LESSON_NAME", lesson.name)
        }
        startActivityForResult(intent, LESSON_DETAIL_REQUEST_CODE)
    }

    // Новый метод для получения всех сохраненных данных
    // Новый метод для получения всех сохраненных данных
    private fun getSavedLessonData(originalName: String, field: String, defaultValue: String): String {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        return sharedPref.getString("${originalName}_$field", defaultValue) ?: defaultValue
    }

    // Добавляем отдельный метод для получения цвета
    private fun getSavedLessonColor(originalName: String, field: String, defaultValue: Int): Int {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        return sharedPref.getInt("${originalName}_$field", defaultValue)
    }

    // Обновляем onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LESSON_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            val updatedName = data?.getStringExtra("UPDATED_LESSON_NAME")
            val updatedTime = data?.getStringExtra("UPDATED_LESSON_TIME")
            val updatedTeacher = data?.getStringExtra("UPDATED_LESSON_TEACHER")
            val updatedRoom = data?.getStringExtra("UPDATED_LESSON_ROOM")
            val updatedType = data?.getStringExtra("UPDATED_LESSON_TYPE")
            val updatedTypeColor = data?.getIntExtra("UPDATED_LESSON_TYPE_COLOR", 0xFF2196F3.toInt())
            val originalName = data?.getStringExtra("ORIGINAL_LESSON_NAME")

            if (updatedName != null && originalName != null) {
                // Сохраняем все данные
                val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("${originalName}_name", updatedName)
                    putString("${originalName}_time", updatedTime ?: "")
                    putString("${originalName}_teacher", updatedTeacher ?: "")
                    putString("${originalName}_room", updatedRoom ?: "")
                    putString("${originalName}_type", updatedType ?: "")
                    putInt("${originalName}_type_color", updatedTypeColor ?: 0xFF2196F3.toInt())
                    apply()
                }

                refreshCurrentSchedule()
            }
        }
    }

    // Добавляем метод для обновления расписания
    private fun refreshCurrentSchedule() {
        val currentDay = getCurrentSelectedDay()
        showDaySchedule(currentDay)
    }
    private fun getSavedLessonName(originalName: String): String {
        val sharedPref = getSharedPreferences("lesson_names", MODE_PRIVATE)
        return sharedPref.getString(originalName, originalName) ?: originalName
    }


}