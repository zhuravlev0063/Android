package com.example.androiddd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.*
import android.view.LayoutInflater


class LessonDetailActivity : AppCompatActivity() {

    private var originalLessonName: String = ""
    private var originalLessonTime: String = ""
    private var originalLessonTeacher: String = ""
    private var originalLessonRoom: String = ""
    private var originalLessonType: String = ""
    private var originalLessonTypeColor: Int = 0

    private var currentLessonName: String = ""
    private var currentLessonTime: String = ""
    private var currentLessonTeacher: String = ""
    private var currentLessonRoom: String = ""
    private var currentLessonType: String = ""
    private var currentLessonTypeColor: Int = 0

    private val colorButtons = mutableListOf<ImageButton>()

    // Данные для типов пар
    private val lessonTypes = listOf(
        LessonType("Лекция", 0xFF2196F3.toInt()),
        LessonType("П/З", 0xFF4CAF50.toInt()),
        LessonType("Лаб", 0xFFFF5722.toInt()),
        LessonType("Семинар", 0xFF9C27B0.toInt()),
        LessonType("Консультация", 0xFFFF9800.toInt()),
        LessonType("Доп занятие", 0xFF607D8B.toInt())
    )

    data class LessonType(val name: String, val color: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_detail)

        // Получаем данные из Intent
        currentLessonName = intent.getStringExtra("LESSON_NAME") ?: ""
        originalLessonName = intent.getStringExtra("ORIGINAL_LESSON_NAME") ?: currentLessonName
        currentLessonTime = intent.getStringExtra("LESSON_TIME") ?: ""
        originalLessonTime = currentLessonTime
        currentLessonTeacher = intent.getStringExtra("LESSON_TEACHER") ?: ""
        originalLessonTeacher = currentLessonTeacher
        currentLessonRoom = intent.getStringExtra("LESSON_ROOM") ?: ""
        originalLessonRoom = currentLessonRoom
        currentLessonType = intent.getStringExtra("LESSON_TYPE") ?: "Лекция"
        originalLessonType = currentLessonType
        currentLessonTypeColor = intent.getIntExtra("LESSON_TYPE_COLOR", 0xFF2196F3.toInt())
        originalLessonTypeColor = currentLessonTypeColor

        // Заполняем данные на экране
        findViewById<EditText>(R.id.lessonName).setText(currentLessonName)
        findViewById<EditText>(R.id.lessonTime).setText(currentLessonTime)
        findViewById<EditText>(R.id.lessonTeacher).setText(currentLessonTeacher)
        findViewById<EditText>(R.id.lessonRoom).setText(currentLessonRoom)

        setupTypeSpinner()
        setupColorPicker()
        setupSaveButton()
        setupTextWatchers()
    }
    // ★★★ ФУНКЦИЯ ДЛЯ КОНВЕРТАЦИИ dp В px ★★★
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    private fun setupTypeSpinner() {
        val spinner = findViewById<Spinner>(R.id.lessonTypeSpinner)

        val typeNames = lessonTypes.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        val currentIndex = typeNames.indexOf(currentLessonType)
        if (currentIndex != -1) {
            spinner.setSelection(currentIndex)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentLessonType = lessonTypes[position].name
                checkForChanges()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun setupColorPicker() {
        val colorPickerLayout = findViewById<LinearLayout>(R.id.colorPickerLayout)

        val colors = listOf(
            0xFF2196F3.toInt(), // Синий
            0xFF4CAF50.toInt(), // Зеленый
            0xFFFF5722.toInt(), // Оранжевый
            0xFF9C27B0.toInt()  // Фиолетовый
        )

        colorButtons.clear()
        colorPickerLayout.removeAllViews()

        // Основные цвета
        colors.forEach { color ->
            val colorButton = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(36),
                    dpToPx(36)
                ).apply {
                    marginEnd = dpToPx(6)
                }

                tag = color
                setBackgroundDrawable(createColorButtonDrawable(color, color == currentLessonTypeColor))
                elevation = 4f // ★★★ ТЕНЬ ДЛЯ КНОПОК ★★★

                setOnClickListener {
                    currentLessonTypeColor = color
                    checkForChanges()
                    updateAllColorButtons()
                }
            }

            colorButtons.add(colorButton)
            colorPickerLayout.addView(colorButton)
        }

        // ★★★ КНОПКА ПАЛИТРЫ С КРАСИВОЙ ИКОНКОЙ ★★★
        val paletteButton = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(36),
                dpToPx(36)
            )

            setImageResource(R.drawable.ic_color_palette) // ★★★ НОВАЯ КРАСИВАЯ ИКОНКА ★★★
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundDrawable(createColorButtonDrawable(0xFFF5F5F5.toInt(), false))
            elevation = 4f // ★★★ ТЕНЬ ★★★

            setOnClickListener {
                showColorPickerDialog()
            }
        }

        colorPickerLayout.addView(paletteButton)
    }
    private fun showColorPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, null)

        // ★★★ ДИАЛОГ БЕЗ СТАНДАРТНЫХ КНОПОК ★★★
        val dialog = android.app.AlertDialog.Builder(this).apply {
            setView(dialogView)
            // Убираем setPositiveButton и setNegativeButton - используем свои кнопки
        }.create()

        // ★★★ НАСТРАИВАЕМ ВНЕШНИЙ ВИД ДИАЛОГА ★★★
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupColorPickerDialog(dialogView, dialog)
        dialog.show()
    }

    private fun setupColorPickerDialog(dialogView: View, dialog: android.app.AlertDialog) {
        val colorGrid = dialogView.findViewById<GridLayout>(R.id.colorGrid)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)

        // ★★★ СОЗДАЕМ МАТРИЦУ 13x13 ЦВЕТОВ ★★★
        val hueSteps = 13
        val saturationSteps = 13

        colorGrid.removeAllViews()
        colorGrid.columnCount = hueSteps

        // Создаем матрицу цветов HSV -> RGB
        for (saturationIndex in 0 until saturationSteps) {
            for (hueIndex in 0 until hueSteps) {
                val hue = (hueIndex * 360f / hueSteps) / 360f
                val saturation = 1f - (saturationIndex * 0.8f / saturationSteps)
                val value = 1.0f

                val color = android.graphics.Color.HSVToColor(floatArrayOf(
                    hue * 360f,
                    saturation,
                    value
                ))

                val colorButton = ImageButton(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = dpToPx(20)
                        height = dpToPx(20)
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(1, 1, 1, 1)
                    }

                    setBackgroundColor(color)
                    setOnClickListener {
                        currentLessonTypeColor = color
                        updateAllColorButtons()

                        // ★★★ ОБНОВЛЯЕМ ПРЕВЬЮ ЦВЕТА ★★★
                        colorPreview.setBackgroundColor(color)

                        // ★★★ ОБНОВЛЯЕМ ПОЛЯ ВВОДА ★★★
                        updateInputFields(dialogView, color)

                        dialog.dismiss()
                        checkForChanges()
                    }
                }
                colorGrid.addView(colorButton)
            }
        }

        // ★★★ ИСПРАВЛЯЕМ БЕСКОНЕЧНЫЙ ЦИКЛ В HEX/RGB ★★★
        val hexInput = dialogView.findViewById<EditText>(R.id.hexInput)
        val redInput = dialogView.findViewById<EditText>(R.id.redInput)
        val greenInput = dialogView.findViewById<EditText>(R.id.greenInput)
        val blueInput = dialogView.findViewById<EditText>(R.id.blueInput)

        // Устанавливаем текущий цвет в поля
        updateInputFields(dialogView, currentLessonTypeColor)
        colorPreview.setBackgroundColor(currentLessonTypeColor)

        // ★★★ ФЛАГ ДЛЯ ПРЕДОТВРАЩЕНИЯ БЕСКОНЕЧНОГО ЦИКЛА ★★★
        var isUpdating = false

        // Слушатель для HEX поля
        hexInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val hex = s?.toString() ?: ""
                if (hex.length == 7 && hex.startsWith("#")) {
                    try {
                        val color = android.graphics.Color.parseColor(hex)
                        val red = (color shr 16) and 0xFF
                        val green = (color shr 8) and 0xFF
                        val blue = color and 0xFF

                        redInput.setText(red.toString())
                        greenInput.setText(green.toString())
                        blueInput.setText(blue.toString())

                        // ★★★ ОБНОВЛЯЕМ ПРЕВЬЮ ЦВЕТА ★★★
                        colorPreview.setBackgroundColor(color)
                    } catch (e: Exception) {
                        // Невалидный HEX
                    }
                }

                isUpdating = false
            }
        })

        // Общий слушатель для RGB полей
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
                    val color = android.graphics.Color.rgb(red, green, blue)
                    hexInput.setText(String.format("#%06X", 0xFFFFFF and color))

                    // ★★★ ОБНОВЛЯЕМ ПРЕВЬЮ ЦВЕТА ★★★
                    colorPreview.setBackgroundColor(color)
                }

                isUpdating = false
            }
        }

        redInput.addTextChangedListener(rgbTextWatcher)
        greenInput.addTextChangedListener(rgbTextWatcher)
        blueInput.addTextChangedListener(rgbTextWatcher)

        // ★★★ КНОПКИ "ГОТОВО" И "ОТМЕНА" ★★★
        val doneButton = dialogView.findViewById<Button>(R.id.doneButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        doneButton.setOnClickListener {
            val hex = hexInput.text.toString()
            if (hex.length == 7 && hex.startsWith("#")) {
                try {
                    currentLessonTypeColor = android.graphics.Color.parseColor(hex)
                    updateAllColorButtons()

                    // ★★★ ОБНОВЛЯЕМ ПРЕВЬЮ ПЕРЕД ЗАКРЫТИЕМ ★★★
                    colorPreview.setBackgroundColor(currentLessonTypeColor)

                    dialog.dismiss()
                    checkForChanges()
                } catch (e: Exception) {
                    Toast.makeText(this, "Неверный формат цвета", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Введите цвет в формате #RRGGBB", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    // ★★★ МЕТОД ДЛЯ ОБНОВЛЕНИЯ ПОЛЕЙ ВВОДА ★★★
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
    private fun setColorToInputs(hexInput: EditText, redInput: EditText, greenInput: EditText, blueInput: EditText, colorPreview: View, color: Int) {
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF

        hexInput.setText(String.format("#%06X", 0xFFFFFF and color))
        redInput.setText(red.toString())
        greenInput.setText(green.toString())
        blueInput.setText(blue.toString())
        colorPreview.setBackgroundColor(color)
    }

    private fun createHexTextWatcher(redInput: EditText, greenInput: EditText, blueInput: EditText, colorPreview: View): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hex = s?.toString() ?: ""
                if (hex.length == 7 && hex.startsWith("#")) {
                    try {
                        val color = android.graphics.Color.parseColor(hex)
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
            }
        }
    }

    private fun createRGBTextWatcher(hexInput: EditText, colorPreview: View, redInput: EditText, greenInput: EditText, blueInput: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val red = redInput.text.toString().toIntOrNull() ?: 0
                val green = greenInput.text.toString().toIntOrNull() ?: 0
                val blue = blueInput.text.toString().toIntOrNull() ?: 0

                if (red in 0..255 && green in 0..255 && blue in 0..255) {
                    val color = android.graphics.Color.rgb(red, green, blue)
                    hexInput.setText(String.format("#%06X", 0xFFFFFF and color))
                    colorPreview.setBackgroundColor(color)
                }
            }
        }
    }

    private fun applyManualColor(hexInput: EditText, dialog: android.app.AlertDialog, colorPreview: View) {
        val hex = hexInput.text.toString()
        if (hex.length == 7 && hex.startsWith("#")) {
            try {
                currentLessonTypeColor = android.graphics.Color.parseColor(hex)
                updateAllColorButtons()

                // ★★★ ОБНОВЛЯЕМ ПРЕВЬЮ ПЕРЕД ЗАКРЫТИЕМ ★★★
                colorPreview.setBackgroundColor(currentLessonTypeColor)

                dialog.dismiss()
                checkForChanges()
            } catch (e: Exception) {
                Toast.makeText(this, "Неверный формат цвета", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Введите цвет в формате #RRGGBB", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createColorButtonDrawable(color: Int, isSelected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f

            if (isSelected) {
                // Выбранный - черная рамка 4dp + цвет
                setStroke(4, 0xFF000000.toInt())
                setColor(color)
            } else {
                // Невыбранный - серая тонкая рамка 1dp + цвет
                setStroke(1, 0xFFCCCCCC.toInt())
                setColor(color)
            }
        }
    }

    private fun updateAllColorButtons() {
        colorButtons.forEach { button ->
            val buttonColor = button.tag as Int
            val isSelected = buttonColor == currentLessonTypeColor
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

        val hasChanges = currentName != currentLessonName ||
                currentTime != currentLessonTime ||
                currentTeacher != currentLessonTeacher ||
                currentRoom != currentLessonRoom ||
                currentLessonType != originalLessonType ||
                currentLessonTypeColor != originalLessonTypeColor

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
        val newName = findViewById<EditText>(R.id.lessonName).text.toString().trim()
        val newTime = findViewById<EditText>(R.id.lessonTime).text.toString().trim()
        val newTeacher = findViewById<EditText>(R.id.lessonTeacher).text.toString().trim()
        val newRoom = findViewById<EditText>(R.id.lessonRoom).text.toString().trim()

        if (newName.isNotBlank() && newTime.isNotBlank()) {
            val sharedPref = getSharedPreferences("lesson_data", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("${originalLessonName}_name", newName)
                putString("${originalLessonName}_time", newTime)
                putString("${originalLessonName}_teacher", newTeacher)
                putString("${originalLessonName}_room", newRoom)
                putString("${originalLessonName}_type", currentLessonType)
                putInt("${originalLessonName}_type_color", currentLessonTypeColor)
                apply()
            }

            val resultIntent = Intent().apply {
                putExtra("UPDATED_LESSON_NAME", newName)
                putExtra("UPDATED_LESSON_TIME", newTime)
                putExtra("UPDATED_LESSON_TEACHER", newTeacher)
                putExtra("UPDATED_LESSON_ROOM", newRoom)
                putExtra("UPDATED_LESSON_TYPE", currentLessonType)
                putExtra("UPDATED_LESSON_TYPE_COLOR", currentLessonTypeColor)
                putExtra("ORIGINAL_LESSON_NAME", originalLessonName)
            }
            setResult(RESULT_OK, resultIntent)

            finish()
        }
    }

    override fun onBackPressed() {
        val newName = findViewById<EditText>(R.id.lessonName).text.toString().trim()
        val newTime = findViewById<EditText>(R.id.lessonTime).text.toString().trim()
        val newTeacher = findViewById<EditText>(R.id.lessonTeacher).text.toString().trim()
        val newRoom = findViewById<EditText>(R.id.lessonRoom).text.toString().trim()

        val hasChanges = newName != currentLessonName ||
                newTime != currentLessonTime ||
                newTeacher != currentLessonTeacher ||
                newRoom != currentLessonRoom ||
                currentLessonType != originalLessonType ||
                currentLessonTypeColor != originalLessonTypeColor

        if (hasChanges) {
            showSaveDialog()
        } else {
            setResult(RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    private fun showSaveDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Сохранение")
            .setMessage("Сохранить изменения?")
            .setPositiveButton("Сохранить") { dialog, which ->
                saveAndExit()
            }
            .setNegativeButton("Не сохранять") { dialog, which ->
                setResult(RESULT_CANCELED)
                finish()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }
}
// ★★★ Extension function для конвертации dp в px ★★★
