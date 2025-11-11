package com.example.androiddd

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androiddd.data.models.Lesson
import com.example.androiddd.utils.dpToPx
import android.graphics.drawable.GradientDrawable
import com.example.androiddd.data.repository.ScheduleRepository

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
        LessonType("Лекция", Color.parseColor("#2196F3")),
        LessonType("П/З", Color.parseColor("#4CAF50")),
        LessonType("Лаб", Color.parseColor("#FF5722")),
        LessonType("Семинар", Color.parseColor("#9C27B0")),
        LessonType("Консультация", Color.parseColor("#FF9800")),
        LessonType("Доп занятие", Color.parseColor("#607D8B")),
        LessonType("КСРС", Color.parseColor("#FFFFFF"))
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
        currentLessonTypeColor = intent.getIntExtra("LESSON_TYPE_COLOR", Color.parseColor("#2196F3"))
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
            Color.parseColor("#2196F3"), // Синий
            Color.parseColor("#4CAF50"), // Зеленый
            Color.parseColor("#FF5722"), // Оранжевый
            Color.parseColor("#9C27B0")  // Фиолетовый
        )

        colorButtons.clear()
        colorPickerLayout.removeAllViews()

        // Основные цвета
        colors.forEach { color ->
            val colorButton = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    36.dpToPx(),
                    36.dpToPx()
                ).apply {
                    marginEnd = 6.dpToPx()
                }
                tag = color
                background = createColorButtonDrawable(color, color == currentLessonTypeColor)
                elevation = 4f
                setOnClickListener {
                    currentLessonTypeColor = color
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
                36.dpToPx(),
                36.dpToPx()
            )
            setImageResource(R.drawable.ic_color_palette)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            background = createColorButtonDrawable(Color.parseColor("#F5F5F5"), false)
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

                val colorButton = ImageButton(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 20.dpToPx()
                        height = 20.dpToPx()
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(1, 1, 1, 1)
                    }
                    setBackgroundColor(color)
                    setOnClickListener {
                        currentLessonTypeColor = color
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

        updateInputFields(dialogView, currentLessonTypeColor)
        colorPreview.setBackgroundColor(currentLessonTypeColor)

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
                    currentLessonTypeColor = Color.parseColor(hex)
                    updateAllColorButtons()
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

    // УДАЛЕНО: setColorToInputs
    // УДАЛЕНО: createHexTextWatcher
    // УДАЛЕНО: createRGBTextWatcher
    // УДАЛЕНО: applyManualColor

    private fun createColorButtonDrawable(color: Int, isSelected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f
            if (isSelected) {
                setStroke(4, Color.BLACK)
                setColor(color)
            } else {
                setStroke(1, Color.parseColor("#CCCCCC"))
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
            val lesson = Lesson(
                time = newTime,
                name = newName,
                teacher = newTeacher,
                room = newRoom,
                type = currentLessonType,
                typeColor = currentLessonTypeColor
            )
            // Используем ScheduleRepository для сохранения
            val repository = ScheduleRepository(this)
            repository.saveLessonData(originalLessonName,originalLessonTime, lesson)

            val resultIntent = Intent().apply {
                putExtra("UPDATED_LESSON_NAME", newName)
                putExtra("UPDATED_LESSON_TIME", newTime)
                putExtra("UPDATED_LESSON_TEACHER", newTeacher)
                putExtra("UPDATED_LESSON_ROOM", newRoom)
                putExtra("UPDATED_LESSON_TYPE", currentLessonType)
                putExtra("UPDATED_LESSON_TYPE_COLOR", currentLessonTypeColor)
                putExtra("ORIGINAL_LESSON_NAME", originalLessonName)
                putExtra("ORIGINAL_LESSON_TIME", originalLessonTime)
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
        AlertDialog.Builder(this)
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