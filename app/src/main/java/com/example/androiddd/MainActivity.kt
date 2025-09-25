package com.example.androiddd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

data class Lesson(
    val time: String,
    val name: String,
    val room: String,
    val teacher: String,
    val type: String,
    val typeColor: Int
)

class MainActivity : AppCompatActivity() {

    private lateinit var daysContainer: LinearLayout
    private lateinit var scheduleContainer: LinearLayout
    private lateinit var weekNumeratorBtn: Button
    private lateinit var weekDenominatorBtn: Button

    private var selectedDayButton: Button? = null
    private var isNumeratorWeek = true
    private var todayDayName: String = "Понедельник" // Сохраняем сегодняшний день

    private val daysData = listOf(
        "Понедельник" to "ПН",
        "Вторник" to "ВТ",
        "Среда" to "СР",
        "Четверг" to "ЧТ",
        "Пятница" to "ПТ",
        "Суббота" to "СБ",
        "Воскресенье" to "ВС"
    )

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

        daysContainer = findViewById(R.id.daysContainer)
        scheduleContainer = findViewById(R.id.scheduleContainer)
        weekNumeratorBtn = findViewById(R.id.weekNumeratorBtn)
        weekDenominatorBtn = findViewById(R.id.weekDenominatorBtn)

        // Определяем сегодняшний день и тип недели
        determineCurrentDayAndWeek()

        setupWeekButtons()
        setupDayButtons() // Теперь кнопки создаются ПЕРВЫМИ

        // Показываем расписание для сегодняшнего дня
        showTodaySchedule()
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

    private fun setupWeekButtons() {
        weekNumeratorBtn.setOnClickListener {
            isNumeratorWeek = true
            updateWeekButtons()
            refreshSchedule()
        }

        weekDenominatorBtn.setOnClickListener {
            isNumeratorWeek = false
            updateWeekButtons()
            refreshSchedule()
        }
        updateWeekButtons()
    }

    private fun updateWeekButtons() {
        if (isNumeratorWeek) {
            weekNumeratorBtn.setBackgroundResource(R.drawable.week_button_selected)
            weekDenominatorBtn.setBackgroundResource(R.drawable.week_button_background)
        } else {
            weekNumeratorBtn.setBackgroundResource(R.drawable.week_button_background)
            weekDenominatorBtn.setBackgroundResource(R.drawable.week_button_selected)
        }
    }

    private fun setupDayButtons() {
        daysData.forEach { (fullName, shortName) ->
            val dayButton = LayoutInflater.from(this).inflate(
                R.layout.layout_day_button,
                daysContainer,
                false
            ) as Button

            dayButton.text = shortName
            dayButton.setOnClickListener {
                selectDayButton(dayButton)
                showDaySchedule(fullName)
            }
            daysContainer.addView(dayButton)

            // Сразу выделяем сегодняшний день при создании кнопки
            if (fullName == todayDayName) {
                selectDayButton(dayButton)
            }
        }

        // Если сегодняшний день не нашелся (например, воскресенье без пар), выделяем первый
        if (selectedDayButton == null && daysContainer.childCount > 0) {
            selectDayButton(daysContainer.getChildAt(0) as Button)
        }
    }

    private fun selectDayButton(button: Button) {
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
        return daysData.firstOrNull { it.second == selectedDayButton?.text }?.first ?: "Понедельник"
    }

    private fun showDaySchedule(dayName: String) {
        scheduleContainer.removeAllViews()

        val dayCard = LayoutInflater.from(this).inflate(
            R.layout.layout_day_card,
            scheduleContainer,
            false
        )

        // Заголовок дня (ТОЛЬКО название дня)
        val dayTitle = dayCard.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = dayName
        dayTitle.setTextColor(0xFF333333.toInt())

        // Пары
        val lessonsContainer = dayCard.findViewById<LinearLayout>(R.id.lessonsContainer)
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
        } else {
            lessons.forEach { lesson ->
                val lessonView = LayoutInflater.from(this).inflate(
                    R.layout.layout_lesson_item,
                    lessonsContainer,
                    false
                )

                lessonView.findViewById<TextView>(R.id.lessonTime).text = lesson.time
                lessonView.findViewById<TextView>(R.id.lessonName).text = lesson.name
                lessonView.findViewById<TextView>(R.id.lessonRoom).text = lesson.room
                lessonView.findViewById<TextView>(R.id.lessonTeacher).text = lesson.teacher

                val typeView = lessonView.findViewById<TextView>(R.id.lessonType)
                typeView.text = lesson.type
                typeView.setBackgroundColor(lesson.typeColor)
                typeView.setTextColor(0xFFFFFFFF.toInt())

                lessonsContainer.addView(lessonView)
            }
        }

        scheduleContainer.addView(dayCard)
    }
}