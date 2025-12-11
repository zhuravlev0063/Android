// File: app/src/main/java/com/example/androiddd/fragments/ScheduleFragment.kt
package com.example.androiddd.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.androiddd.LessonDetailActivity
import com.example.androiddd.MainActivity
import com.example.androiddd.R
import com.example.androiddd.data.models.Lesson
import java.util.*
import kotlin.collections.emptyList

class ScheduleFragment : Fragment() {

    private lateinit var scheduleContainer: LinearLayout
    private var dayNameForSchedule: String = ""
    private lateinit var scheduleNumerator: Map<String, List<Lesson>>
    private lateinit var scheduleDenominator: Map<String, List<Lesson>>

    companion object {
        private const val ARG_DAY_NAME = "arg_day_name"
        private const val ARG_SCHEDULE_NUMERATOR = "arg_schedule_numerator"
        private const val ARG_SCHEDULE_DENOMINATOR = "arg_schedule_denominator"

        // Фабричный метод для создания фрагмента с аргументами
        fun newInstance(
            dayName: String,
            numeratorSchedule: Map<String, List<Lesson>>,
            denominatorSchedule: Map<String, List<Lesson>>
        ): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle().apply {
                putString(ARG_DAY_NAME, dayName)
                // Для передачи Map или List часто используют Serializable или Parcelable
                // или передают ключи и получают данные в самой Activity.
                // Для простоты передадим как Serializable (убедитесь, что Lesson implements Serializable или Parcelable)
                putSerializable(ARG_SCHEDULE_NUMERATOR, numeratorSchedule as java.io.Serializable)
                putSerializable(ARG_SCHEDULE_DENOMINATOR, denominatorSchedule as java.io.Serializable)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayNameForSchedule = it.getString(ARG_DAY_NAME, "")
            scheduleNumerator = it.getSerializable(ARG_SCHEDULE_NUMERATOR) as Map<String, List<Lesson>>
            scheduleDenominator = it.getSerializable(ARG_SCHEDULE_DENOMINATOR) as Map<String, List<Lesson>>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        scheduleContainer = view.findViewById(R.id.scheduleContainer)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ScheduleFragment", "onViewCreated вызван, dayNameForSchedule: $dayNameForSchedule") // <-- ДОБАВЬ ЭТОТ ЛОГ
        if (dayNameForSchedule.isNotEmpty()) {
            showScheduleForDay(dayNameForSchedule) // <-- Используем dayNameForSchedule из arguments
        }
    }

    // File: app/src/main/java/com/example/androiddd/fragments/ScheduleFragment.kt
// ...
    private fun showScheduleForDay(dayName: String) {
        Log.d("ScheduleFragment", "showScheduleForDay вызван для дня: $dayName") // <-- ДОБАВЬ ЭТОТ ЛОГ
        scheduleContainer.removeAllViews()

        val dayCard = LayoutInflater.from(context).inflate(
            R.layout.layout_day_card,
            scheduleContainer,
            false
        )

        val dayTitle = dayCard.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = dayName
        dayTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface))

        val addLessonButton = dayCard.findViewById<Button>(R.id.addLessonButton)
        addLessonButton.setOnClickListener {
            Log.d("ScheduleFragment", "Кнопка добавления пары нажата для дня: $dayName") // <-- ДОБАВЬ ЭТОТ ЛОГ
            (requireActivity() as MainActivity).showAddLessonDialog(dayName)
        }

        val lessonsContainer = dayCard.findViewById<LinearLayout>(R.id.lessonsContainer)

        // --- ДОБАВЬ ЛОГИ ---
        val isNumeratorWeek = (requireActivity() as MainActivity).isCurrentWeekNumerator() // Вызов метода из MainActivity
        Log.d("ScheduleFragment", "showScheduleForDay: isNumeratorWeek для текущего смещения: $isNumeratorWeek, день: $dayName") // <-- НОВЫЙ ЛОГ
        // ---


        val schedule = if (isNumeratorWeek) {
            // (requireActivity() as MainActivity).getScheduleNumerator() // <-- Если ты создал fun getScheduleNumerator()
            (requireActivity() as MainActivity).scheduleNumerator // <-- Если ты сделал val scheduleNumerator public
        } else {
            // (requireActivity() as MainActivity).getScheduleDenominator() // <-- Если ты создал fun getScheduleDenominator()
            (requireActivity() as MainActivity).scheduleDenominator // <-- Если ты сделал val scheduleDenominator public
        }
        val lessons = schedule[dayName] ?: emptyList()

        Log.d("ScheduleFragment", "showScheduleForDay: Найдено пар из жёсткого расписания для $dayName (numerator=$isNumeratorWeek): ${lessons.size}")

        // Собираем все пары: из жёсткого расписания и добавленные пользователем
        val allLessons = mutableListOf<Lesson>()
        allLessons.addAll(lessons)

        // Загружаем добавленные пользователем пары
        val userAddedLessons = (requireActivity() as MainActivity).getUserAddedLessonsForDay(dayName) // Вызов метода из MainActivity
        Log.d("ScheduleFragment", "showScheduleForDay: Найдено добавленных пар для $dayName: ${userAddedLessons.size}") // <-- НОВЫЙ ЛОГ
        allLessons.addAll(userAddedLessons)

        // Сортируем пары по времени (для корректного отображения)
        allLessons.sortBy { lesson ->
            lesson.time.substringBefore("-").replace(":", "").toIntOrNull() ?: Int.MAX_VALUE
        }

        if (allLessons.isEmpty()) {
            val message = when (dayName) {
                getString(R.string.day_sunday) -> getString(R.string.empty_schedule_message_sunday)
                else -> getString(R.string.empty_schedule_message_default)
            }
            val emptyText = TextView(requireContext()).apply {
                text = message
                textSize = 18f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.empty_list_text_color))
                gravity = android.view.Gravity.CENTER
                setPadding(0, resources.getDimensionPixelSize(R.dimen.empty_list_padding_vertical), 0, resources.getDimensionPixelSize(R.dimen.empty_list_padding_vertical))
            }
            lessonsContainer.addView(emptyText)
        } else {
            allLessons.forEach { lesson ->
                val lessonView = LayoutInflater.from(context).inflate(
                    R.layout.layout_lesson_item,
                    lessonsContainer,
                    false
                )

                val savedName = (requireActivity() as MainActivity).getSavedLessonData(lesson.name, lesson.time, "name", lesson.name)
                val savedTime = (requireActivity() as MainActivity).getSavedLessonData(lesson.name, lesson.time, "time", lesson.time)
                val savedTeacher = (requireActivity() as MainActivity).getSavedLessonData(lesson.name, lesson.time, "teacher", lesson.teacher)
                val savedRoom = (requireActivity() as MainActivity).getSavedLessonData(lesson.name, lesson.time, "room", lesson.room)
                val savedType = (requireActivity() as MainActivity).getSavedLessonData(lesson.name, lesson.time, "type", lesson.type)
                val savedTypeColor = (requireActivity() as MainActivity).getSavedLessonColor(lesson.name, lesson.time, "type_color", lesson.typeColor)


                lessonView.findViewById<TextView>(R.id.lessonTime).text = savedTime
                lessonView.findViewById<TextView>(R.id.lessonName).text = savedName
                lessonView.findViewById<TextView>(R.id.lessonRoom).text = savedRoom
                lessonView.findViewById<TextView>(R.id.lessonTeacher).text = savedTeacher
                val typeView = lessonView.findViewById<TextView>(R.id.lessonType)
                typeView.text = savedType
                typeView.setBackgroundColor(savedTypeColor)
                // --- ИСПРАВЛЕНО: Используем requireContext() ---
                typeView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnPrimary))
                // ---

                lessonView.setOnClickListener {
                    Log.d("ScheduleFragment", "lessonView.setOnClickListener сработал для: ${lesson.name}") // <-- Лог 1
                    try {
                        Log.d("ScheduleFragment", "Вызов openLessonDetails с парой: ${lesson.name}") // <-- Лог 2
                        (requireActivity() as MainActivity).openLessonDetails(lesson) // <-- Убедись, что openLessonDetails есть в MainActivity
                    } catch (e: Exception) {
                        Log.e("ScheduleFragment", "Ошибка при вызове openLessonDetails: ${e.message}", e) // <-- Лог ошибки
                    }
                }
                lessonsContainer.addView(lessonView)
                val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_item) // Загружаем анимацию
                lessonView.startAnimation(fadeInAnimation) // Запускаем анимацию на view
            }
        }
        scheduleContainer.addView(dayCard)
    }
// ...

    // --- ВРЕМЕННО: ПЕРЕНОС НЕКОТОРЫХ МЕТОДОВ СЮДА ИЛИ ИХ ВЫЗОВ ЧЕРЕЗ requireActivity() ---
    // Эти методы должны быть в MainActivity, но для демонстрации вызываются здесь.
    // Лучше передать нужные данные через arguments или использовать интерфейс.
    private fun determineWeekType(): Boolean {
        // Реализуйте логику определения типа недели здесь или получите из arguments
        // Например, можно передать isNumeratorWeek как Boolean в arguments
        return (requireActivity() as MainActivity).isCurrentWeekNumerator()
    }

    private fun getUserAddedLessonsForDay(dayName: String): List<Lesson> {
        return (requireActivity() as MainActivity).getUserAddedLessonsForDay(dayName)
    }
    // --- КОНЕЦ ВРЕМЕННОГО ПЕРЕНОСА ---

    // Методы для вызова из MainActivity
    fun updateSchedule(dayName: String) {
        showScheduleForDay(dayName)
    }
}