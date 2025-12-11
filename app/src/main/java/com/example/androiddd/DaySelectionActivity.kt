// File: app/src/main/java/com/example/androiddd/DaySelectionActivity.kt
package com.example.androiddd

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class DaySelectionActivity : AppCompatActivity() {

    private lateinit var dayListView: ListView

    private lateinit var buttonBack: ImageButton
    private lateinit var dayNames: List<String>
    private lateinit var shortDayNames: List<String>

    private lateinit var dayAdapter: DayListAdapter

    // Кол-во дней для отображения (например, 30 дней)
    private val numberOfDaysToShow = 30

    // Генерируем список дат
    private val dateFormatForDisplay = SimpleDateFormat("EEE, d MMM", Locale.getDefault()) // "Пн, 9 Дек"
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // "Понедельник"


    // Data class для хранения информации о дате
    data class DateInfo(
        val date: Date,
        val dayOfWeekName: String,
        val formattedDate: String // "Пн, 9 Дек"
    )

    private fun generateDatesList(numDays: Int): List<DateInfo> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<DateInfo>()
        //val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // <-- "Понедельник", "Вторник", ...


        for (i in 0 until numDays) {
            val currentDate = calendar.time
            val dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK)

            // --- ИСПРАВЛЕНИЕ: Получаем строку из ресурсов ---
            val dayOfWeekName = when (dayOfWeekInt) {
                Calendar.MONDAY -> getString(R.string.day_monday)    // "Понедельник"
                Calendar.TUESDAY -> getString(R.string.day_tuesday)  // "Вторник"
                Calendar.WEDNESDAY -> getString(R.string.day_wednesday) // "Среда"
                Calendar.THURSDAY -> getString(R.string.day_thursday) // "Четверг"
                Calendar.FRIDAY -> getString(R.string.day_friday)   // "Пятница"
                Calendar.SATURDAY -> getString(R.string.day_saturday) // "Суббота"
                Calendar.SUNDAY -> getString(R.string.day_sunday)   // "Воскресенье"
                else -> getString(R.string.day_monday) // fallback
            }
            // ---
            val formattedDate = dateFormatForDisplay.format(currentDate)
            dates.add(DateInfo(currentDate, dayOfWeekName, formattedDate))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_selection)

        // --- ПОЛУЧЕНИЕ ПАРАМЕТРА ИЗ INTENT ---
        val receivedDayName = intent.getStringExtra("CURRENT_DAY_NAME") // Получаем переданный день
        if (receivedDayName != null) {
            Log.d("DaySelectionActivity", "Получен день из MainActivity: $receivedDayName")
            // Можно использовать receivedDayName, например, для установки заголовка или выделения дня
            // title = "Выбор дня (сегодня: $receivedDayName)" // Пример использования
        } else {
            Log.w("DaySelectionActivity", "Параметр CURRENT_DAY_NAME не передан или равен null")
        }
        buttonBack = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            // Обработка нажатия на кнопку "назад"
            onBackPressed() // Вызывает стандартное поведение "назад", закрывает Activity
        }
        // --- КОНЕЦ ПОЛУЧЕНИЯ ПАРАМЕТРА ---

        // --- ИНИЦИАЛИЗАЦИЯ ЧЛЕНОВ КЛАССА ВНУТРИ onCreate ---
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

        val datesList = generateDatesList(numberOfDaysToShow)

        dayListView = findViewById(R.id.dayListView)

        dayAdapter = DayListAdapter(this, datesList)
        dayListView.adapter = dayAdapter

        dayListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedDateInfo = datesList[position]
            Log.d("DaySelectionActivity", "Выбрана дата: ${selectedDateInfo.formattedDate}, день недели: ${selectedDateInfo}")

            // Возвращаем результат в вызывающую Activity
            val resultIntent = Intent().apply {
                // Возвращаем форматированную строку даты или саму дату в определённом формате
                putExtra("SELECTED_DAY_NAME", selectedDateInfo.dayOfWeekName) // <-- Используем константу из MainActivity
                putExtra("SELECTED_DATE_MILLIS", selectedDateInfo.date.time) // <-- Ключ для даты в millis
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Обработка нажатия на "стрелку назад" в ActionBar
                onBackPressed() // Вызывает стандартное поведение "назад", закрывает Activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Внутренний класс адаптера (inner class, чтобы иметь доступ к ресурсам)
    inner class DayListAdapter(
        context: Context,
        private val dates: List<DateInfo>
    ) : BaseAdapter() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getCount(): Int {
            return dates.size
        }

        override fun getItem(position: Int): Any {
            return dates[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // Убираем внутренний класс ViewHolder
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val textView: TextView

            if (convertView == null) {
                view = inflater.inflate(R.layout.list_item_day, parent, false)
                textView = view.findViewById(R.id.dayNameTextView) // Предполагаем, что в layout_item_day есть TextView с id dayNameTextView
                view.tag = textView
            } else {
                view = convertView
                textView = view.tag as TextView
            }

            val dateInfo = dates[position]
            textView.text = dateInfo.formattedDate // Отображаем "Пн, 9 Дек"

            return view
        }
    }
}