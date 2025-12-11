package com.example.androiddd

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.androiddd.utils.dpToPx
import android.util.Log
import android.view.MenuItem
import com.example.androiddd.utils.LocaleManager
import java.util.regex.Pattern

class LessonDetailActivity : AppCompatActivity()  {

    private lateinit var buttonContactTeacher: Button
    private var originalLessonName: String = ""
    private var originalLessonTime: String = ""
    private var originalLessonTeacher: String = ""
    private var originalLessonRoom: String = ""
    private var originalLessonType: String = ""
    private var originalLessonTypeColor: Int = 0
    private var lessonName: String = ""
    private var lessonTime: String = ""
    private var lessonTeacher: String = ""
    private var lessonRoom: String = ""
    private var lessonType: String = ""
    private var lessonTypeColor: Int = 0
    private lateinit var lessonNameEditText: EditText
    private lateinit var lessonTimeEditText: EditText
    private lateinit var lessonTeacherEditText: EditText
    private lateinit var lessonRoomEditText: EditText
    private lateinit var lessonTypeSpinner: Spinner
    private lateinit var colorPickerLayout: LinearLayout
    private lateinit var saveButton: ImageButton
    private val colorButtons = mutableListOf<ImageButton>()
    private lateinit var buttonBack: ImageButton

    // Признак новой пары
    private var isNewLesson: Boolean = false
    // День недели (для новой пары)
    private var dayNameForNewLesson: String = ""

    // Данные для типов пар - теперь lateinit var, инициализируется в onCreate
    private lateinit var lessonTypes: List<LessonType>
    private val TAG = "LessonDetailActivity"
    data class LessonType(val name: String, val color: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_detail)

        buttonBack = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            // Обработка нажатия на кнопку "назад"
            onBackPressed() // Вызывает стандартное поведение "назад", закрывает Activity
        }

        lessonNameEditText = findViewById(R.id.lessonName)
        lessonTimeEditText = findViewById(R.id.lessonTime)
        lessonTeacherEditText = findViewById(R.id.lessonTeacher)
        lessonRoomEditText = findViewById(R.id.lessonRoom)
        lessonTypeSpinner = findViewById(R.id.lessonTypeSpinner)
        colorPickerLayout = findViewById(R.id.colorPickerLayout)
        saveButton = findViewById(R.id.saveButton)
        buttonContactTeacher = findViewById(R.id.buttonContactTeacher)

        // --- ИНИЦИАЛИЗАЦИЯ lessonTypes ВНУТРИ onCreate ---
        lessonTypes = listOf(
            LessonType(getString(R.string.lesson_type_lecture), ContextCompat.getColor(this, R.color.lesson_type_lecture)),
            LessonType(getString(R.string.lesson_type_practical), ContextCompat.getColor(this, R.color.lesson_type_practical)),
            LessonType(getString(R.string.lesson_type_lab), ContextCompat.getColor(this, R.color.lesson_type_lab)),
            LessonType(getString(R.string.lesson_type_seminar), ContextCompat.getColor(this, R.color.lesson_type_seminar)),
            LessonType(getString(R.string.lesson_type_consultation), ContextCompat.getColor(this, R.color.lesson_type_consultation)),
            LessonType(getString(R.string.lesson_type_extra), ContextCompat.getColor(this, R.color.lesson_type_extra))
        )
        // --- КОНЕЦ ИНИЦИАЛИЗАЦИИ ---

        // Получаем признак новой пары и день
        isNewLesson = intent.getBooleanExtra(MainActivity.EXTRA_IS_NEW_LESSON, false)
        dayNameForNewLesson = intent.getStringExtra(MainActivity.EXTRA_DAY_NAME) ?: ""

        if (isNewLesson) {
            // Режим новой пары
            lessonName = ""
            lessonTime = ""
            lessonTeacher = ""
            lessonRoom = ""
            lessonType = getString(R.string.lesson_type_lecture) // Используем строковый ресурс
            lessonTypeColor = ContextCompat.getColor(this, R.color.lesson_type_lecture) // Используем цвет из ресурсов

            originalLessonName = lessonName
            originalLessonTime = lessonTime
            originalLessonTeacher = lessonTeacher
            originalLessonRoom = lessonRoom
            originalLessonType = lessonType
            originalLessonTypeColor = lessonTypeColor
            // Очищаем поля ввода
            lessonNameEditText.setText("")
            lessonTimeEditText.setText("")
            lessonTeacherEditText.setText("")
            lessonRoomEditText.setText("")
            setupTypeSpinner()
            setupColorPicker()
            setupSaveButton()
            setupTextWatchers()
        } else {
            // Режим редактирования существующей пары
            // Получаем начальные данные из Intent (это могут быть *оригинальные* или *последние сохранённые*, если MainActivity передаёт последние)
            val initialName = intent.getStringExtra("LESSON_NAME") ?: ""
            val initialTime = intent.getStringExtra("LESSON_TIME") ?: ""
            val initialTeacher = intent.getStringExtra("LESSON_TEACHER") ?: ""
            val initialRoom = intent.getStringExtra("LESSON_ROOM") ?: ""
            val initialType = intent.getStringExtra("LESSON_TYPE") ?: getString(R.string.lesson_type_lecture) // Используем строковый ресурс
            val initialTypeColor = intent.getIntExtra("LESSON_TYPE_COLOR", ContextCompat.getColor(this, R.color.lesson_type_lecture)) // Используем цвет из ресурсов

            // --- ЧТЕНИЕ ПОСЛЕДНИХ СОХРАНЁННЫХ ДАННЫХ ИЗ SharedPreferences ---
            // Используем initialName и initialTime как *ключи* для получения *последних* значений
            lessonName = getSavedLessonData(initialName, initialTime, "name", initialName)
            lessonTime = getSavedLessonData(initialName, initialTime, "time", initialTime)
            lessonTeacher = getSavedLessonData(initialName, initialTime, "teacher", initialTeacher)
            lessonRoom = getSavedLessonData(initialName, initialTime, "room", initialRoom)
            lessonType = getSavedLessonData(initialName, initialTime, "type", initialType)
            lessonTypeColor = getSavedLessonColor(initialName, initialTime, "type_color", initialTypeColor)
            // ---
            originalLessonName = initialName // <-- Сохраняем оригинальное имя (для ключа при сохранении)
            originalLessonTime = initialTime // <-- Сохраняем оригинальное время (для ключа при сохранении)
            originalLessonTeacher = initialTeacher // <-- Сохраняем оригинального препода (для сравнения)
            originalLessonRoom = initialRoom // <-- Сохраняем оригинальную аудиторию (для сравнения)
            originalLessonType = initialType // <-- Сохраняем оригинальный тип (для сравнения)
            originalLessonTypeColor = initialTypeColor // <-- Сохраняем оригинальный цвет (для сравнения)

            Log.d("LessonDetailActivity", "Загружены данные: Name=$lessonName, Time=$lessonTime, Teacher=$lessonTeacher, Room=$lessonRoom, Type=$lessonType, TypeColor=$lessonTypeColor")

            // Заполняем данные на экране *последними* сохранёнными значениями
            lessonNameEditText.setText(lessonName)
            lessonTimeEditText.setText(lessonTime)
            lessonTeacherEditText.setText(lessonTeacher)
            lessonRoomEditText.setText(lessonRoom)
            setupTypeSpinner()
            setupColorPicker()
            setupSaveButton()
            setupTextWatchers()
            setupContactTeacherButton()
        }
    }
    private fun getSavedLessonData(originalName: String, originalTime: String, field: String, defaultValue: String): String {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        // Заменяем пробелы и двоеточия на подчеркивание ДЛЯ КЛЮЧА
        val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
        val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
        val key = "${safeOriginalName}_${safeOriginalTime}_$field"
        val result = sharedPref.getString(key, defaultValue) ?: defaultValue
        Log.d("LessonDetailActivity", "getSavedLessonData: key=$key, result=$result") // <-- Добавим лог
        return result
    }

    private fun getSavedLessonColor(originalName: String, originalTime: String, field: String, defaultValue: Int): Int {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        // Заменяем пробелы и двоеточия на подчеркивание ДЛЯ КЛЮЧА
        val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
        val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
        val key = "${safeOriginalName}_${safeOriginalTime}_$field"
        val result = sharedPref.getInt(key, defaultValue)
        Log.d("LessonDetailActivity", "getSavedLessonColor: key=$key, result=$result") // <-- Добавим лог
        return result
    }
    private fun setupContactTeacherButton() {
        // Получаем email из поля преподавателя или каким-то другим способом (например, из базы данных по имени)
        // Пока что, просто возьмём текст из поля ввода как есть. В реальном приложении здесь будет логика извлечения email.
        val teacherName = lessonTeacherEditText.text.toString().trim()
        // Пример: Ищем email в формате "Имя Фамилия (email@example.com)" или просто "email@example.com" в имени препода
        val emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}")
        val matcher = emailPattern.matcher(teacherName)

        val teacherEmail =
            lessonTeacherEditText.text.toString().trim() // Извлекаем email из EditText
        Log.d("LessonDetailActivity", "Полученный email преподавателя: '$teacherEmail'") // <-- Добавь это


        if (teacherEmail.isNotEmpty()) {
            buttonContactTeacher.visibility = View.VISIBLE
            buttonContactTeacher.setOnClickListener {
                val subject =
                    getString(R.string.email_subject_template, lessonName, lessonTime)
                val body = getString(
                    R.string.email_body_template,
                    lessonName,
                    lessonTime,
                    lessonTeacher,
                    lessonRoom
                )

                // --- ИЗМЕНЕНИЕ: Используем ACTION_SEND вместо ACTION_SENDTO ---
                val emailIntent = Intent(Intent.ACTION_SEND).apply { // <-- Заменили на SEND
                    type = "message/rfc822" // Указываем MIME-тип для email
                    putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf(teacherEmail)
                    ) // Передаём получателя как массив
                    putExtra(Intent.EXTRA_SUBJECT, subject) // Тема
                    putExtra(Intent.EXTRA_TEXT, body)       // Текст
                }

                // Проверяем, есть ли приложение, которое может обработать это намерение
                if (emailIntent.resolveActivity(packageManager) != null) {
                    startActivity(emailIntent) // <-- Запуск неявного намерения
                } else {
                    // Если нет подходящего приложения, показываем Toast
                    Toast.makeText(this, getString(R.string.error_no_email_app), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }else {
            Log.w("LessonDetailActivity", "Email преподавателя пуст или недействителен: '$teacherEmail'")
            buttonContactTeacher.visibility = View.GONE
        }
    }
    private fun setupTypeSpinner() {
        val spinner = findViewById<Spinner>(R.id.lessonTypeSpinner)
        val typeNames = lessonTypes.map { it.name }
        // ИСПОЛЬЗУЕМ НОВЫЙ layout
        val adapter = ArrayAdapter(this, R.layout.spinner_item_type, typeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Или layout для dropdown
        spinner.adapter = adapter
        val currentIndex = typeNames.indexOf(lessonType)
        if (currentIndex != -1) {
            spinner.setSelection(currentIndex)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lessonType = lessonTypes[position].name
                checkForChanges()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    override fun attachBaseContext(base: Context?) {
        Log.d(TAG, "LessonDetailActivity.attachBaseContext called with base context locale: ${base?.resources?.configuration?.locales?.get(0)?.language ?: "null"}")
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
    private fun setupColorPicker() {
        val colorPickerLayout = findViewById<LinearLayout>(R.id.colorPickerLayout)
        val colors = listOf(
            ContextCompat.getColor(this, R.color.lesson_type_lecture), // Синий
            ContextCompat.getColor(this, R.color.lesson_type_practical), // Зеленый
            ContextCompat.getColor(this, R.color.lesson_type_lab), // Оранжевый
            ContextCompat.getColor(this, R.color.lesson_type_seminar)  // Фиолетовый
        )

        colorButtons.clear()
        colorPickerLayout.removeAllViews()

        // Основные цвета
        colors.forEach { color ->
            val colorButton = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    36.dpToPx(this@LessonDetailActivity),
                    36.dpToPx(this@LessonDetailActivity)
                ).apply {
                    marginEnd = 6.dpToPx(this@LessonDetailActivity)
                }
                tag = color
                background = createColorButtonDrawable(color, color == lessonTypeColor)
                elevation = 4f
                setOnClickListener {
                    lessonTypeColor = color
                    checkForChanges()
                    updateAllColorButtons()
                }
            }
            colorButtons.add(colorButton)
            colorPickerLayout.addView(colorButton)
        }

        // Кнопка палитры
        val paletteButton = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                36.dpToPx(this@LessonDetailActivity),
                36.dpToPx(this@LessonDetailActivity)
            )
            setImageResource(R.drawable.ic_color_palette)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            background = createColorButtonDrawable(ContextCompat.getColor(this@LessonDetailActivity, R.color.colorSurfaceVariant), false)
            elevation = 4f
            setOnClickListener {
                showColorPickerDialog()
            }
        }
        colorPickerLayout.addView(paletteButton)
    }

    private fun showColorPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, null)
        val dialog = AlertDialog.Builder(this).apply {
            setView(dialogView)
        }.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        setupColorPickerDialog(dialogView, dialog)
        dialog.show()
    }

    private fun setupColorPickerDialog(dialogView: View, dialog: AlertDialog) {
        val colorGrid = dialogView.findViewById<GridLayout>(R.id.colorGrid)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)

        val hueSteps = 13
        val saturationSteps = 13
        colorGrid.removeAllViews()
        colorGrid.columnCount = hueSteps

        for (saturationIndex in 0 until saturationSteps) {
            for (hueIndex in 0 until hueSteps) {
                val hue = (hueIndex * 360f / hueSteps) / 360f
                val saturation = 1f - (saturationIndex * 0.8f / saturationSteps)
                val value = 1.0f
                val color = Color.HSVToColor(floatArrayOf(
                    hue * 360f,
                    saturation,
                    value
                ))

                val colorButton = ImageButton(this@LessonDetailActivity).apply { // <-- Передаём Activity как Context
                    layoutParams = GridLayout.LayoutParams().apply {
                        // Если dpToPx требует Context:
                        // width = 20.dpToPx(this@LessonDetailActivity) // Пример
                        // height = 20.dpToPx(this@LessonDetailActivity) // Пример
                        width = resources.getDimensionPixelSize(R.dimen.dialog_color_button_size) // Лучше так
                        height = resources.getDimensionPixelSize(R.dimen.dialog_color_button_size) // Лучше так
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(
                            resources.getDimensionPixelSize(R.dimen.dialog_color_button_margin),
                            resources.getDimensionPixelSize(R.dimen.dialog_color_button_margin),
                            resources.getDimensionPixelSize(R.dimen.dialog_color_button_margin),
                            resources.getDimensionPixelSize(R.dimen.dialog_color_button_margin)
                        )
                    }
                    setBackgroundColor(color)
                    setOnClickListener {
                        lessonTypeColor = color
                        updateAllColorButtons()
                        colorPreview.setBackgroundColor(color)
                        updateInputFields(dialogView, color)
                        dialog.dismiss()
                        checkForChanges()
                    }
                }
                colorGrid.addView(colorButton)
            }
        }

        val hexInput = dialogView.findViewById<EditText>(R.id.hexInput)
        val redInput = dialogView.findViewById<EditText>(R.id.redInput)
        val greenInput = dialogView.findViewById<EditText>(R.id.greenInput)
        val blueInput = dialogView.findViewById<EditText>(R.id.blueInput)

        updateInputFields(dialogView, lessonTypeColor)
        colorPreview.setBackgroundColor(lessonTypeColor)

        var isUpdating = false

        hexInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true
                val hex = s?.toString() ?: ""
                if (hex.length == 7 && hex.startsWith("#")) {
                    try {
                        val color = Color.parseColor(hex)
                        val red = (color shr 16) and 0xFF
                        val green = (color shr 8) and 0xFF
                        val blue = color and 0xFF
                        redInput.setText(red.toString())
                        greenInput.setText(green.toString())
                        blueInput.setText(blue.toString())
                        colorPreview.setBackgroundColor(color)
                    } catch (e: Exception) {
                        // Невалидный HEX
                    }
                }
                isUpdating = false
            }
        })

        val rgbTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true
                val red = redInput.text.toString().toIntOrNull() ?: 0
                val green = greenInput.text.toString().toIntOrNull() ?: 0
                val blue = blueInput.text.toString().toIntOrNull() ?: 0
                if (red in 0..255 && green in 0..255 && blue in 0..255) {
                    val color = Color.rgb(red, green, blue)
                    hexInput.setText(String.format("#%06X", 0xFFFFFF and color))
                    colorPreview.setBackgroundColor(color)
                }
                isUpdating = false
            }
        }
        redInput.addTextChangedListener(rgbTextWatcher)
        greenInput.addTextChangedListener(rgbTextWatcher)
        blueInput.addTextChangedListener(rgbTextWatcher)

        val doneButton = dialogView.findViewById<Button>(R.id.doneButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        doneButton.setOnClickListener {
            val hex = hexInput.text.toString()
            if (hex.length == 7 && hex.startsWith("#")) {
                try {
                    lessonTypeColor = Color.parseColor(hex)
                    updateAllColorButtons()
                    colorPreview.setBackgroundColor(lessonTypeColor)
                    dialog.dismiss()
                    checkForChanges()
                } catch (e: Exception) {
                    Toast.makeText(this@LessonDetailActivity, getString(R.string.error_invalid_color_format), Toast.LENGTH_SHORT).show() // Используем Activity Context
                }
            } else {
                Toast.makeText(this@LessonDetailActivity, getString(R.string.error_enter_color_format), Toast.LENGTH_SHORT).show() // Используем Activity Context
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun updateInputFields(dialogView: View, color: Int) {
        val hexInput = dialogView.findViewById<EditText>(R.id.hexInput)
        val redInput = dialogView.findViewById<EditText>(R.id.redInput)
        val greenInput = dialogView.findViewById<EditText>(R.id.greenInput)
        val blueInput = dialogView.findViewById<EditText>(R.id.blueInput)

        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF

        hexInput.setText(String.format("#%06X", 0xFFFFFF and color))
        redInput.setText(red.toString())
        greenInput.setText(green.toString())
        blueInput.setText(blue.toString())
    }

    private fun createColorButtonDrawable(color: Int, isSelected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.getDimension(R.dimen.lesson_detail_input_margin_bottom) // Используем dimen
            if (isSelected) {
                setStroke(4, ContextCompat.getColor(this@LessonDetailActivity, R.color.colorOnSurface)) // Используем цвет из ресурсов
                setColor(color)
            } else {
                setStroke(1, ContextCompat.getColor(this@LessonDetailActivity, R.color.colorOutline)) // Используем цвет из ресурсов
                setColor(color)
            }
        }
    }

    private fun updateAllColorButtons() {
        colorButtons.forEach { button ->
            val buttonColor = button.tag as Int
            val isSelected = buttonColor == lessonTypeColor
            button.background = createColorButtonDrawable(buttonColor, isSelected)
        }
    }

    private fun setupTextWatchers() {
        val lessonNameEditText = findViewById<EditText>(R.id.lessonName)
        val lessonTimeEditText = findViewById<EditText>(R.id.lessonTime)
        val lessonTeacherEditText = findViewById<EditText>(R.id.lessonTeacher)
        val lessonRoomEditText = findViewById<EditText>(R.id.lessonRoom)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkForChanges()
            }
        }

        lessonNameEditText.addTextChangedListener(textWatcher)
        lessonTimeEditText.addTextChangedListener(textWatcher)
        lessonTeacherEditText.addTextChangedListener(textWatcher)
        lessonRoomEditText.addTextChangedListener(textWatcher)
    }

    private fun checkForChanges() {
        val currentName = findViewById<EditText>(R.id.lessonName).text.toString().trim()
        val currentTime = findViewById<EditText>(R.id.lessonTime).text.toString().trim()
        val currentTeacher = findViewById<EditText>(R.id.lessonTeacher).text.toString().trim()
        val currentRoom = findViewById<EditText>(R.id.lessonRoom).text.toString().trim()

        val hasChanges = currentName != lessonName ||
                currentTime != lessonTime ||
                currentTeacher != lessonTeacher ||
                currentRoom != lessonRoom ||
                lessonType != originalLessonType ||
                lessonTypeColor != originalLessonTypeColor

        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        if (hasChanges && currentName.isNotBlank() && currentTime.isNotBlank()) {
            saveButton.isEnabled = true
            saveButton.alpha = 1.0f
        } else {
            saveButton.isEnabled = false
            saveButton.alpha = 0.3f
        }
    }

    private fun setupSaveButton() {
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveAndExit()
        }
    }

    private fun saveAndExit() {
        val newName = lessonNameEditText.text.toString().trim()
        val newTime = lessonTimeEditText.text.toString().trim()
        val newTeacher = lessonTeacherEditText.text.toString().trim()
        val newRoom = lessonRoomEditText.text.toString().trim()
        // val newType и newTypeColor получены из других полей (colorButtons, spinner)

        if (newName.isNotBlank() && newTime.isNotBlank()) {
            val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
            val editor = sharedPref.edit()

            if (isNewLesson && dayNameForNewLesson.isNotEmpty()) {
                // --- СОХРАНЕНИЕ НОВОЙ ПАРЫ ---
                // Генерируем уникальный идентификатор
                val uniqueId = java.util.UUID.randomUUID().toString()
                val uniqueName = "${dayNameForNewLesson}_${newTime}_$uniqueId" // Комбинируем день, время и UUID

                // Сохраняем данные новой пары под уникальным ключом (имя_время_uuid)
                editor.putString("${uniqueName}_name", newName)
                editor.putString("${uniqueName}_time", newTime)
                editor.putString("${uniqueName}_teacher", newTeacher)
                editor.putString("${uniqueName}_room", newRoom)
                editor.putString("${uniqueName}_type", lessonType) // Используем текущий выбранный тип
                editor.putInt("${uniqueName}_type_color", lessonTypeColor) // Используем текущий выбранный цвет

                // Добавляем новую пару в список для дня
                addToUserAddedLessonsForDay(dayNameForNewLesson, newTime, newName, newTeacher, newRoom, lessonType, lessonTypeColor, uniqueId) // Передаем uniqueId
                // ---
            } else {
                // --- СОХРАНЕНИЕ СУЩЕСТВУЮЩЕЙ ПАРЫ ---
                // Используем *оригинальные* (или *начальные*) имена/время как ключи для *обновления* существующей пары
                // Эти значения были получены из Intent при открытии
                val originalNameFromIntent = intent.getStringExtra("ORIGINAL_LESSON_NAME") ?: lessonName // Используем lessonName как fallback
                val originalTimeFromIntent = intent.getStringExtra("ORIGINAL_LESSON_TIME") ?: lessonTime // Используем lessonTime как fallback

                // Заменяем пробелы и двоеточия на подчеркивание ДЛЯ ОРИГИНАЛЬНЫХ КЛЮЧЕЙ
                val safeOriginalName = originalNameFromIntent.replace(" ", "_").replace(":", "_").replace("-", "_")
                val safeOriginalTime = originalTimeFromIntent.replace(" ", "_").replace(":", "_").replace("-", "_")

                editor.putString("${safeOriginalName}_${safeOriginalTime}_name", newName)
                editor.putString("${safeOriginalName}_${safeOriginalTime}_time", newTime)
                editor.putString("${safeOriginalName}_${safeOriginalTime}_teacher", newTeacher)
                editor.putString("${safeOriginalName}_${safeOriginalTime}_room", newRoom)
                editor.putString("${safeOriginalName}_${safeOriginalTime}_type", lessonType) // Используем текущий выбранный тип
                editor.putInt("${safeOriginalName}_${safeOriginalTime}_type_color", lessonTypeColor) // Используем текущий выбранный цвет
                // ---
            }
            editor.apply()

            val resultIntent = Intent().apply {
                putExtra("UPDATED_LESSON_NAME", newName)
                putExtra("UPDATED_LESSON_TIME", newTime)
                putExtra("UPDATED_LESSON_TEACHER", newTeacher)
                putExtra("UPDATED_LESSON_ROOM", newRoom)
                putExtra("UPDATED_LESSON_TYPE", lessonType)
                putExtra("UPDATED_LESSON_TYPE_COLOR", lessonTypeColor)
                putExtra(MainActivity.EXTRA_DAY_NAME, if (isNewLesson) dayNameForNewLesson else null)
                putExtra(MainActivity.EXTRA_IS_NEW_LESSON, isNewLesson)
                if (!isNewLesson) {
                    // putExtra("ORIGINAL_LESSON_NAME", originalNameFromIntent) // <-- Не нужно передавать обратно
                    // putExtra("ORIGINAL_LESSON_TIME", originalTimeFromIntent) // <-- Не нужно передавать обратно
                    putExtra("ORIGINAL_LESSON_NAME", originalLessonName) // <-- Всё-таки передаём, чтобы MainActivity знал, что обновлять
                    putExtra("ORIGINAL_LESSON_TIME", originalLessonTime) // <-- Всё-таки передаём, чтобы MainActivity знал, что обновлять
                }
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            // Показать Toast, если поля пусты
            Toast.makeText(this, getString(R.string.error_blank_fields), Toast.LENGTH_SHORT).show()
        }
    }

    // Метод для добавления новой пары в JSON-список для дня
    private fun addToUserAddedLessonsForDay(dayName: String, lessonTime: String, lessonName: String, lessonTeacher: String, lessonRoom: String, lessonType: String, lessonTypeColor: Int, uniqueId: String) {
        val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
        val key = "user_added_lessons_$dayName"
        val currentJsonString = sharedPref.getString(key, "[]") ?: "[]"
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
            put("uniqueId", uniqueId)
        }
        jsonArray.put(lessonJson)

        sharedPref.edit().putString(key, jsonArray.toString()).apply()
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



    override fun onBackPressed() {
        // Логика сохранения при нажатии "назад", аналогично предыдущей версии
        val newName = lessonNameEditText.text.toString().trim()
        val newTime = lessonTimeEditText.text.toString().trim()
        val newTeacher = lessonTeacherEditText.text.toString().trim()
        val newRoom = lessonRoomEditText.text.toString().trim()

        val hasChanges = newName != lessonName || // Сравниваем с начальным значением
                newTime != lessonTime || // Сравниваем с начальным значением
                newTeacher != lessonTeacher || // Сравниваем с начальным значением
                newRoom != lessonRoom || // Сравниваем с начальным значением
                lessonType != intent.getStringExtra("LESSON_TYPE") ?: getString(R.string.lesson_type_lecture) || // Сравниваем с начальным типом
                lessonTypeColor != intent.getIntExtra("LESSON_TYPE_COLOR", ContextCompat.getColor(this, R.color.lesson_type_lecture)) // Сравниваем с начальным цветом

        if (hasChanges) {
            showSaveDialog()
        } else {
            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title_save))
            .setMessage(getString(R.string.dialog_message_save))
            .setPositiveButton(getString(R.string.dialog_button_save)) { dialog, which ->
                saveAndExit()
            }
            .setNegativeButton(getString(R.string.dialog_button_discard)) { dialog, which ->
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .setNeutralButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }
}