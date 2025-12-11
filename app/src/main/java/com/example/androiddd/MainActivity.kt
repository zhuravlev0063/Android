package com.example.androiddd

import com.example.androiddd.utils.ThemeManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.androiddd.data.models.Lesson
import com.example.androiddd.data.repository.AuthRepository
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.util.*
import java.text.SimpleDateFormat
import android.widget.PopupMenu
import android.view.MenuInflater
import android.view.View
import com.example.androiddd.utils.LocaleManager
import android.util.Log
import android.content.Context
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.androiddd.data.repository.ScheduleRepository
import com.example.androiddd.fragments.ScheduleFragment

class MainActivity : AppCompatActivity() {
    private lateinit var authRepository: AuthRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var daysContainer: LinearLayout
    private lateinit var prevWeekBtn: Button
    private lateinit var nextWeekBtn: Button
    private lateinit var weekRangeText: TextView
    private lateinit var weekTypeText: TextView
    private lateinit var menuButton: ImageButton
    private var selectedDayButton: LinearLayout? = null
    private var currentWeekOffset = 0
    private var todayDayName: String = "" // <-- Уже есть как член класса
    private var calendarDays: Map<Int, String> = mapOf()
    private lateinit var dayNames: List<String>
    private lateinit var shortDayNames: List<String>
    private val TAG = "MainActivity"
    private val DAY_SELECTION_REQUEST_CODE = 1002 // Уникальный код запроса


    val scheduleNumerator by lazy {
        mapOf(
            getString(R.string.day_monday) to listOf(
                Lesson("15:40-17:00", "Распределенные задачи и алгоритмы", "Ауд. 129", "Приходько Т.А.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("17:10-18:30", "Нейросетевые и нечеткие модели", "Ауд. А301б", "Городецкий Э.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("18:40-20:00", "Модели интеллектуальных систем", "Ауд. А301б", "Городецкий Э.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab))
            ),
            getString(R.string.day_tuesday) to listOf(
                Lesson("Весь день", "КСРС", "Самостоятельная работа", "Самообучение", "КСРС", ContextCompat.getColor(this, R.color.lesson_type_seminar))
            ),
            getString(R.string.day_wednesday) to listOf(
                Lesson("12:40-14:00", "Основы компьютерной графики", "Ауд. 103а", "Гаркуша О.В.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("14:10-15:30", "Информационная безопасность", "Ауд. 131", "Шиян В.И.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("15:40-17:00", "Информационная безопасность", "Ауд. 128", "Шиян В.И.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("17:10-18:30", "Программирование для мобильных платформ", "Ауд. 128", "Приходько Т.А.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
            ),
            getString(R.string.day_thursday) to listOf(
                Lesson("14:10-15:30", "Основы компьютерной графики", "Ауд. A305", "Гаркуша О.В.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("15:40-17:00", "Тестирование и отладка ПО", "Ауд. 147", "Городецкий Э.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("17:10-18:30", "Модели интеллектуальных систем", "Ауд. 131", "Костенко К.И.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("18:40-20:00", "Программирование для мобильных платформ","Ауд. 102а","Яхонтов А.А.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab))
            ),
            getString(R.string.day_friday) to listOf(
                Lesson("14:10-15:30", "Тестирование и отладка ПО","Ауд. A305","Городецкий Э.Р.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("15:40-17:00", "Нейросетевые и нечеткие модели","Ауд. 129", "Руденко О.В.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("17:10-18:30", "Основы военной подготовки", "Ауд. 100C", "Крылов Д.С.", getString(R.string.lesson_type_practical), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
            ),
            getString(R.string.day_saturday) to listOf(
                Lesson("8:00-9:20", "Бэкенд разработка", "Ауд. 128", "Кесян Г.Р.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("9:30-10:50", "Распределенные задачи и алгоритмы", "Ауд. 102a", "Яхонтов А.А.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("11:10-12:30", "Алгоритмы цифровой обработки мультимедиа", "Ауд. 101", "Крамаренко А.А.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("12:40-14:00", "Основы военной подготовки", "Ауд. 131", "Крылов Д.С.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
            ),
            getString(R.string.day_sunday) to emptyList()
        )
    }

    val scheduleDenominator by lazy {
        mapOf(
            getString(R.string.day_monday) to listOf(
                Lesson("15:40-17:00", "Распределенные задачи и алгоритмы", "Ауд. 129", "Приходько Т.А.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("17:10-18:30", "Нейросетевые и нечеткие модели", "Ауд. А301б", "Городецкий Э.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("18:40-20:00", "Модели интеллектуальных систем", "Ауд. А301б", "Городецкий Э.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab))
            ),
            getString(R.string.day_tuesday) to listOf(
                Lesson("Весь день", "КСРС", "Самостоятельная работа", "Самообучение", "КСРС", ContextCompat.getColor(this, R.color.lesson_type_seminar))
            ),
            getString(R.string.day_wednesday) to listOf(
                Lesson("15:40-17:00", "Информационная безопасность", "Ауд. 128", "Шиян В.И.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("17:10-18:30", "Программирование для мобильных платформ", "Ауд. 128", "Приходько Т.А.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
            ),
            getString(R.string.day_thursday) to listOf(
                Lesson("14:10-15:30", "Алгоритмы цифровой обработки мультимедиа", "Ауд. 128", "Крамаренко А.А.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("15:40-17:00", "Тестирование и отладка ПО", "Ауд. 147", "Городецкий Э.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("17:10-18:30", "Модели интеллектуальных систем", "Ауд. 131", "Костенко К.И.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("18:40-20:00", "Программирование для мобильных платформ","Ауд. 102а","Яхонтов А.А.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab))
            ),
            getString(R.string.day_friday) to listOf(
                Lesson("14:10-15:30", "Информационная безопасность", "Ауд. A305", "Шиян В.И.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("15:40-17:00", "Нейросетевые и нечеткие модели","Ауд. 129", "Руденко О.В.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("17:10-18:30", "Основы военной подготовки", "Ауд. 100C", "Крылов Д.С.", getString(R.string.lesson_type_practical), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
            ),
            getString(R.string.day_saturday) to listOf(
                Lesson("9:30-10:50", "Распределенные задачи и алгоритмы", "Ауд. 102a", "Яхонтов А.А.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("11:10-12:30", "Бэкенд разработка", "Ауд. 102", "Кесян Г.Р.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
                Lesson("12:40-14:00", "Основы военной подготовки", "Ауд. 131", "Крылов Д.С.", getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
                Lesson("14:10-15:30", "Алгоритмы цифровой обработки мультимедиа", "Ауд. 128", "Крамаренко А.А.", getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
            ),
            getString(R.string.day_sunday) to emptyList()
        )
    }


    companion object {
        private const val LESSON_DETAIL_REQUEST_CODE = 1001
        // Новый ключ для передачи дня недели
        const val EXTRA_DAY_NAME = "day_name"
        // Новый ключ для передачи признака новой пары
        const val EXTRA_IS_NEW_LESSON = "is_new_lesson"
        const val EXTRA_SELECTED_DAY_NAME = "extra_selected_day_name"
        private const val DAY_SELECTION_REQUEST_CODE = 1002
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false } // Показываем splash до тех пор, пока не выполнится условие
        setTheme(R.style.Theme_Androiddd) // Убедитесь, что это имя соответствует вашей теме AppCompat
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- ИНИЦИАЛИЗАЦИЯ РЕСУРСОВ ПОСЛЕ super.onCreate(savedInstanceState) И ДО ИХ ИСПОЛЬЗОВАНИЯ ---
        todayDayName = getString(R.string.day_monday)
        calendarDays = mapOf(
            Calendar.MONDAY to getString(R.string.day_monday),
            Calendar.TUESDAY to getString(R.string.day_tuesday),
            Calendar.WEDNESDAY to getString(R.string.day_wednesday),
            Calendar.THURSDAY to getString(R.string.day_thursday),
            Calendar.FRIDAY to getString(R.string.day_friday),
            Calendar.SATURDAY to getString(R.string.day_saturday),
            Calendar.SUNDAY to getString(R.string.day_sunday)
        )
        dayNames = listOf(
            getString(R.string.day_monday),
            getString(R.string.day_tuesday),
            getString(R.string.day_wednesday),
            getString(R.string.day_thursday),
            getString(R.string.day_friday),
            getString(R.string.day_saturday),
            getString(R.string.day_sunday)
        )

        shortDayNames = listOf(
            getString(R.string.day_short_monday),
            getString(R.string.day_short_tuesday),
            getString(R.string.day_short_wednesday),
            getString(R.string.day_short_thursday),
            getString(R.string.day_short_friday),
            getString(R.string.day_short_saturday),
            getString(R.string.day_short_sunday)
        )
        // --- КОНЕЦ ИНИЦИАЛИЗАЦИИ ---

        authRepository = AuthRepository(this)
        scheduleRepository = ScheduleRepository(this)
        menuButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            showPopupMenu(it)
        }
        daysContainer = findViewById(R.id.daysContainer)
        prevWeekBtn = findViewById(R.id.prevWeekBtn)
        nextWeekBtn = findViewById(R.id.nextWeekBtn)
        weekRangeText = findViewById(R.id.weekRangeText)
        weekTypeText = findViewById(R.id.weekTypeText)

        determineCurrentDay()
        setupWeekNavigation()
        setupDayButtons()
        showTodaySchedule()
    }
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view) // 'this' - это Context
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.main_menu, popup.menu)
        val logoutItem = popup.menu.findItem(R.id.action_logout)
        val loginItem = popup.menu.findItem(R.id.action_login)

        if (authRepository.isLoggedIn()) {
            // Пользователь вошёл: показываем "Выйти", скрываем "Войти"
            logoutItem.isVisible = true
            loginItem.isVisible = false
        } else {
            // Пользователь не вошёл: показываем "Войти", скрываем "Выйти"
            logoutItem.isVisible = false
            loginItem.isVisible = true
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> { // <-- Новый обработчик
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_logout -> {
                    authRepository.logout()
                    // Обновляем UI (например, скрываем расписание, показываем приветствие)
                    // findViewById<TextView>(R.id.welcomeText).visibility = View.VISIBLE
                    true
                }

                R.id.action_login -> { // <-- Новый обработчик
                    // Перейти на LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    // Не вызываем finish(), чтобы пользователь мог вернуться в MainActivity
                    true
                }

                R.id.action_select_day -> { // <-- Новый обработчик
                   val intent = Intent(this, DaySelectionActivity::class.java) // <-- Явное намерение
                    // Передаём один параметр: текущий день недели
                    // intent.putExtra("CURRENT_DAY_NAME", todayDayName) // Передаём день недели как строку
                    // Используем startActivityForResult, чтобы получить результат обратно
                    startActivityForResult(intent, DAY_SELECTION_REQUEST_CODE) // Убедись, что DAY_SELECTION_REQUEST_CODE определён
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
    private fun determineCurrentDay() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        todayDayName = calendarDays[currentDayOfWeek] ?: getString(R.string.day_monday) // <-- Используем ресурс по умолчанию
    }
    override fun attachBaseContext(base: Context?) {
        Log.d(TAG, "MainActivity.attachBaseContext called with base context locale: ${base?.resources?.configuration?.locales?.get(0)?.language ?: "null"}")
        val updatedContext = if (base != null) {
            val savedLanguage = LocaleManager.getLanguage(base) // Получаем сохранённый язык
            Log.d(TAG, "LocaleManager.getLanguage returned: $savedLanguage")
            if (savedLanguage != null) {
                // Если язык сохранён, применяем его к контексту Activity
                LocaleManager.updateContext(base, savedLanguage)
            } else {
                // Иначе используем системный
                base
            }
        } else {
            base
        }
        super.attachBaseContext(updatedContext)
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
        val weekType = if (isNumerator) getString(R.string.navigation_week_type_numerator) else getString(R.string.navigation_week_type_denominator) // <-- Используем ресурсы
        weekTypeText.text = weekType

        if (currentWeekOffset == 0) {
            weekRangeText.setTextColor(ContextCompat.getColor(this, R.color.week_navigation_text_current))
            weekTypeText.setTextColor(ContextCompat.getColor(this, R.color.week_navigation_text_current))
        } else {
            weekRangeText.setTextColor(ContextCompat.getColor(this, R.color.week_navigation_text_default))
            weekTypeText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant))
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
        val months = listOf(getString(R.string.month_jan), getString(R.string.month_feb), getString(R.string.month_mar), getString(R.string.month_apr), getString(R.string.month_may), getString(R.string.month_jun),
            getString(R.string.month_jul), getString(R.string.month_aug), getString(R.string.month_sep), getString(R.string.month_oct), getString(R.string.month_nov), getString(R.string.month_dec))
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
        for (i in 0 until 7) {
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val fullName = dayNames[i]
            val dayButton = LayoutInflater.from(this).inflate(
                R.layout.layout_day_button,
                daysContainer,
                false
            ) as LinearLayout

            dayButton.findViewById<TextView>(R.id.dayShortName).text = shortDayNames[i]
            dayButton.findViewById<TextView>(R.id.dayDate).text = dayNumber.toString()

            dayButton.setOnClickListener {
                selectDayButton(dayButton)
                showDaySchedule(fullName)
            }

            // --- УСТАНАВЛИВАЕМ ТЕГ ---
            dayButton.tag = fullName // <-- Устанавливаем имя дня недели как тег
            // --- КОНЕЦ УСТАНОВКИ ТЕГА ---

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
        val dayNames = listOf(getString(R.string.day_monday), getString(R.string.day_tuesday), getString(R.string.day_wednesday), getString(R.string.day_thursday), getString(R.string.day_friday), getString(R.string.day_saturday), getString(R.string.day_sunday))
        for (i in 0 until daysContainer.childCount) {
            if (daysContainer.getChildAt(i) == selectedDayButton) {
                return dayNames[i]
            }
        }
        return getString(R.string.day_monday)
    }

    private fun showDaySchedule(dayName: String) {
        Log.d("MainActivity", "showDaySchedule вызван для дня: $dayName") // <-- ДОБАВЬ ЭТОТ ЛОГ
        // Заменяем фрагмент в контейнере
        val fragment = ScheduleFragment.newInstance(dayName, scheduleNumerator, scheduleDenominator) // <-- Передаём расписания
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // <-- Убедитесь, что у вас есть FrameLayout с id fragment_container в activity_main.xml
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) // Опциональная анимация
            .commit()
    }

    // MainActivity.kt
// ...
    // Метод для определения типа недели (числитель/знаменатель) на основе currentWeekOffset
    fun isCurrentWeekNumerator(): Boolean { // <-- Сделаем его public, чтобы ScheduleFragment мог вызвать
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
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset) // <-- ИСПОЛЬЗУЕТСЯ currentWeekOffset
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val diffInMillis = calendar.timeInMillis - academicYearStart.timeInMillis
        val diffInWeeks = (diffInMillis / (1000 * 60 * 60 * 24 * 7)).toInt()

        val isNumerator = (diffInWeeks % 2 == 0)
        Log.d("MainActivity", "isCurrentWeekNumerator: currentWeekOffset=$currentWeekOffset, diffInWeeks=$diffInWeeks, isNumerator=$isNumerator") // <-- НОВЫЙ ЛОГ
        return isNumerator
    }
    // ...
    fun getUserAddedLessonsForDay(dayName: String): List<Lesson> {
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
    fun getSavedLessonData(originalName: String, originalTime: String, field: String, defaultValue: String): String {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        // Заменяем пробелы и двоеточия на подчеркивание
        val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
        val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
        val key = "${safeOriginalName}_${safeOriginalTime}_$field"
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    fun getSavedLessonColor(originalName: String, originalTime: String, field: String, defaultValue: Int): Int {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        // Заменяем пробелы и двоеточия на подчеркивание (исправлено!)
        val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
        val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
        val key = "${safeOriginalName}_${safeOriginalTime}_$field" // <-- Используем безопасные имена
        return sharedPref.getInt(key, defaultValue)
    }
    fun openLessonDetails(lesson: Lesson) { // Используем существующий метод
        Log.d("MainActivity", "openLessonDetails вызван для: ${lesson.name}, время: ${lesson.time}") // <-- ДОБАВЬ ЭТОТ ЛОГ

        val intent = Intent(this, LessonDetailActivity::class.java).apply {
            putExtra("LESSON_NAME", lesson.name)
            putExtra("LESSON_TIME", lesson.time)
            putExtra("LESSON_TEACHER", lesson.teacher)
            putExtra("LESSON_ROOM", lesson.room)
            putExtra("LESSON_TYPE", lesson.type)
            putExtra("LESSON_TYPE_COLOR", lesson.typeColor)
            putExtra("ORIGINAL_LESSON_NAME", lesson.name)
            putExtra("ORIGINAL_LESSON_TIME", lesson.time)
            putExtra(EXTRA_IS_NEW_LESSON, false)
        }
        Log.d("MainActivity", "Запуск LessonDetailActivity с intent") // <-- ДОБАВЬ ЭТОТ ЛОГ
        startActivityForResult(intent, LESSON_DETAIL_REQUEST_CODE)
    }
    fun showAddLessonDialog(dayName: String) {
        Log.d("MainActivity", "showAddLessonDialog вызван для дня: $dayName")
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
        nameEditText.setText(getString(R.string.default_new_lesson_name))
        timeEditText.setText(getString(R.string.default_new_lesson_time))
        teacherEditText.setText(getString(R.string.default_new_lesson_teacher))
        roomEditText.setText(getString(R.string.default_new_lesson_room))

        val lessonTypes = listOf(getString(R.string.lesson_type_lecture), getString(R.string.lesson_type_practical), getString(R.string.lesson_type_lab), getString(R.string.lesson_type_seminar), getString(R.string.lesson_type_consultation), getString(R.string.lesson_type_extra))
        val adapter = android.widget.ArrayAdapter(this, R.layout.spinner_item_type, lessonTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Или свой layout для dropdown
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

    private fun addNewLesson(dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String) {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Генерируем действительно уникальный идентификатор
        val uniqueId = java.util.UUID.randomUUID().toString()
        val uniqueName = "${dayName}_${lessonTime}_$uniqueId" // Комбинируем день, время и UUID

        // Сохраняем данные новой пары под уникальным ключом (имя_время_uuid)
        editor.putString("${uniqueName}_name", lessonName)
        editor.putString("${uniqueName}_time", lessonTime)
        editor.putString("${uniqueName}_teacher", lessonTeacher)
        editor.putString("${uniqueName}_room", lessonRoom)
        editor.putString("${uniqueName}_type", lessonType)
        editor.putInt("${uniqueName}_type_color", ContextCompat.getColor(this, R.color.lesson_type_lecture)) // Цвет по умолчанию

        // Добавляем новую пару в список для дня
        addToUserAddedLessonsForDay(editor, dayName, lessonTime, lessonName, lessonTeacher, lessonRoom, lessonType, ContextCompat.getColor(this, R.color.lesson_type_lecture), uniqueId) // Передаем uniqueId

        editor.apply()
        refreshCurrentSchedule()
    }
    private fun addToUserAddedLessonsForDay(editor: android.content.SharedPreferences.Editor, dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String, lessonTypeColor: Int, uniqueId: String) {
        val key = "user_added_lessons_$dayName"
        val currentJsonString = this@MainActivity.getSharedPreferences("lesson_data", MODE_PRIVATE).getString(key, "[]") ?: "[]" // <-- Используем this@MainActivity
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
            put("uniqueId", uniqueId) // Добавляем уникальный ID
        }
        jsonArray.put(lessonJson)
        editor.putString(key, jsonArray.toString())
    }
    // File: app/src/main/java/com/example/androiddd/MainActivity.kt
// ...
    // УБРАНО: override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // --- Обработка LESSON_DETAIL_REQUEST_CODE ---
            LESSON_DETAIL_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    Log.d("MainActivity", "Обработка результата из LessonDetailActivity (редактирование/добавление пары)")

                    val updatedName = data?.getStringExtra("UPDATED_LESSON_NAME")
                    val updatedTime = data?.getStringExtra("UPDATED_LESSON_TIME")
                    val updatedTeacher = data?.getStringExtra("UPDATED_LESSON_TEACHER")
                    val updatedRoom = data?.getStringExtra("UPDATED_LESSON_ROOM")
                    val updatedType = data?.getStringExtra("UPDATED_LESSON_TYPE")
                    val updatedTypeColor = data?.getIntExtra("UPDATED_LESSON_TYPE_COLOR", ContextCompat.getColor(this, R.color.lesson_type_lecture))
                    val originalName = data?.getStringExtra("ORIGINAL_LESSON_NAME")
                    val originalTime = data?.getStringExtra("ORIGINAL_LESSON_TIME")

                    Log.d("MainActivity", "Полученные обновлённые данные: Name=$updatedName, Time=$updatedTime, OriginalName=$originalName, OriginalTime=$originalTime, TypeColor=$updatedTypeColor")

                    if (updatedName != null && originalName != null && originalTime != null) {
                        Log.d("MainActivity", "Обновление существующей пары по ключу: ${originalName}_${originalTime}")
                        try {
                            val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
                                val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
                                putString("${safeOriginalName}_${safeOriginalTime}_name", updatedName)
                                putString("${safeOriginalName}_${safeOriginalTime}_time", updatedTime ?: "")
                                putString("${safeOriginalName}_${safeOriginalTime}_teacher", updatedTeacher ?: "")
                                putString("${safeOriginalName}_${safeOriginalTime}_room", updatedRoom ?: "")
                                putString("${safeOriginalName}_${safeOriginalTime}_type", updatedType ?: "")
                                val colorToSave = updatedTypeColor ?: ContextCompat.getColor(this@MainActivity, R.color.lesson_type_lecture)
                                putInt("${safeOriginalName}_${safeOriginalTime}_type_color", colorToSave)
                                apply()
                            }
                            Log.d("MainActivity", "Данные обновлены в SharedPreferences. Вызов refreshCurrentSchedule.")
                            refreshCurrentSchedule() // Обновляем отображение
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Ошибка при обновлении пары в onActivityResult: ${e.message}", e)
                        }
                    } else {
                        Log.w("MainActivity", "Данные обновления отсутствуют или не хватает originalName/originalTime для обновления. Name=$updatedName, OrigName=$originalName, OrigTime=$originalTime")
                    }
                }
            }
            // --- Конец обработки LESSON_DETAIL_REQUEST_CODE ---

            // --- СУЩЕСТВУЮЩАЯ ОБРАБОТКА DAY_SELECTION_REQUEST_CODE ---
            DAY_SELECTION_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    Log.d("MainActivity", "Обработка результата из DaySelectionActivity")
                    // --- ПОЛУЧАЕМ ИМЯ ДНЯ И КОНКРЕТНУЮ ДАТУ ---
                    val selectedDayName = data?.getStringExtra("SELECTED_DAY_NAME")
                    val selectedDateMillis = data?.getLongExtra("SELECTED_DATE_MILLIS", -1L) // <-- НОВЫЙ КЛЮЧ
                    // ---
                    Log.d("MainActivity", "Получено имя выбранного дня: $selectedDayName, получена дата (millis): $selectedDateMillis") // <-- НОВЫЙ ЛОГ

                    if (selectedDayName != null && selectedDateMillis != null && selectedDateMillis != -1L) { // <-- Проверка на дату
                        Log.d("MainActivity", "Выбран день: $selectedDayName, дата: ${Date(selectedDateMillis)}") // <-- ЛОГ с датой

                        // --- ЛОГИКА ОБНОВЛЕНИЯ НЕДЕЛИ (РАСЧЁТ currentWeekOffset НА ОСНОВЕ КАЛЕНДАРНОЙ НЕДЕЛИ С ВЫБРАННОЙ ДАТОЙ) ---
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.timeInMillis = selectedDateMillis // <-- Устанавливаем на выбранную дату

                        // Найдем понедельник недели, содержащей выбранную дату
                        selectedCalendar.firstDayOfWeek = Calendar.MONDAY // Убедимся, что понедельник - первый день недели
                        selectedCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

                        // Найдем понедельник *текущей* недели (где currentWeekOffset = 0)
                        val currentCalendar = Calendar.getInstance()
                        currentCalendar.firstDayOfWeek = Calendar.MONDAY // Убедимся, что понедельник - первый день недели
                        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

                        // Рассчитаем смещение
                        val targetWeekOfYear = selectedCalendar.get(Calendar.WEEK_OF_YEAR)
                        val targetYear = selectedCalendar.get(Calendar.YEAR)
                        val currentWeekOfYear = currentCalendar.get(Calendar.WEEK_OF_YEAR)
                        val currentYear = currentCalendar.get(Calendar.YEAR)

                        var newWeekOffset = 0
                        if (targetYear == currentYear) {
                            newWeekOffset = targetWeekOfYear - currentWeekOfYear
                        } else if (targetYear > currentYear) {
                            val weeksInCurrentYear = currentCalendar.getActualMaximum(Calendar.WEEK_OF_YEAR)
                            newWeekOffset = (weeksInCurrentYear - currentWeekOfYear) + targetWeekOfYear
                        } else { // targetYear < currentYear
                            currentCalendar.set(Calendar.YEAR, targetYear)
                            val weeksInTargetYear = currentCalendar.getActualMaximum(Calendar.WEEK_OF_YEAR)
                            newWeekOffset = targetWeekOfYear - (weeksInTargetYear - currentWeekOfYear)
                        }

                        Log.d("MainActivity", "Рассчитано смещение недели для даты $selectedDateMillis (${Date(selectedDateMillis)}): $newWeekOffset") // <-- НОВЫЙ ЛОГ
                        // --- КОНЕЦ ЛОГИКИ ОБНОВЛЕНИЯ ---

                        // --- УСТАНОВКА СМЕЩЕНИЯ ---
                        currentWeekOffset = newWeekOffset // <-- УСТАНОВКА currentWeekOffset
                        updateWeekDisplay() // <-- Обновление отображения недели (кнопки +/-, диапазон)
                        refreshDayButtons() // <-- Обновление кнопок дней (пересоздание, сброс выделения)
                        // --- КОНЕЦ УСТАНОВКИ ---

                        // --- ВЫБОР КНОПКИ ДНЯ ---
                        // Цикл по дням, как в setupDayButtons, но без вызова showDaySchedule
                        val dayNamesList = listOf(
                            getString(R.string.day_monday),
                            getString(R.string.day_tuesday),
                            getString(R.string.day_wednesday),
                            getString(R.string.day_thursday),
                            getString(R.string.day_friday),
                            getString(R.string.day_saturday),
                            getString(R.string.day_sunday)
                        )
                        for (i in dayNamesList.indices) {
                            val childView = daysContainer.getChildAt(i)
                            if (childView.tag == selectedDayName) { // <-- Сравниваем с тегом, который ты устанавливаешь в setupDayButtons
                                selectDayButton(childView as LinearLayout) // <-- Только выделение кнопки
                                break // Нашли и выделили
                            }
                        }
                        // --- КОНЕЦ ВЫБОРА КНОПКИ ---

                        // --- ПОКАЗАТЬ РАСПИСАНИЕ ДЛЯ ВЫБРАННОГО ДНЯ ---
                        Log.d("MainActivity", "Вызов showDaySchedule из onActivityResult для дня: $selectedDayName (из даты $selectedDateMillis)") // <-- НОВЫЙ ЛОГ
                        showDaySchedule(selectedDayName) // <-- ВОТ ЭТОТ ВЫЗОВ ДОЛЖЕН БЫТЬ ПОСЛЕДНИМ
                        // --- КОНЕЦ ПОКАЗА РАСПИСАНИЯ ---
                    } else {
                        Log.e("MainActivity", "Ошибка: selectedDayName или selectedDateMillis равны null или -1.")
                        Toast.makeText(this, "Ошибка получения дня или даты", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    // ... // Новый метод для выбора кнопки дня по дате
    private fun selectDayButtonByDate(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val calendarInstance = Calendar.getInstance()
        calendarInstance.time = date
        val dayOfWeekInCalendar = calendarInstance.get(Calendar.DAY_OF_WEEK)

        // Найдем имя дня недели в нашей локализованной коллекции
        val dayNameForDate = when (dayOfWeekInCalendar) {
            Calendar.MONDAY -> dayNames[0] // getString(R.string.day_monday)
            Calendar.TUESDAY -> dayNames[1] // getString(R.string.day_tuesday)
            Calendar.WEDNESDAY -> dayNames[2] // и т.д.
            Calendar.THURSDAY -> dayNames[3]
            Calendar.FRIDAY -> dayNames[4]
            Calendar.SATURDAY -> dayNames[5]
            Calendar.SUNDAY -> dayNames[6]
            else -> return // Неизвестный день недели
        }

        for (i in 0 until daysContainer.childCount) {
            val childView = daysContainer.getChildAt(i)
            if (childView.tag == dayNameForDate) {
                // Снимаем выделение с предыдущей кнопки
                selectedDayButton?.setBackgroundResource(R.drawable.day_button_background)
                // Устанавливаем выделение на новую кнопку
                childView.setBackgroundResource(R.drawable.day_button_selected)
                selectedDayButton = childView as LinearLayout
                break // Нашли и выделили
            }
        }
    }
    private fun saveNewLessonForDay(editor: android.content.SharedPreferences.Editor, dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String, lessonTypeColor: Int) {
        val key = "user_added_lessons_$dayName"
        val currentJsonString = this@MainActivity.getSharedPreferences("lesson_data", MODE_PRIVATE).getString(key, "[]") ?: "[]"
        val jsonArray = try {
            org.json.JSONArray(currentJsonString)
        } catch (e: Exception) {
            org.json.JSONArray()
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
    private fun refreshCurrentSchedule() {
        val currentDay = getCurrentSelectedDay()
        Log.d("MainActivity", "refreshCurrentSchedule вызван для дня: $currentDay")
        showDaySchedule(currentDay)
    }
}