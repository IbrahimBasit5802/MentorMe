package com.ibrahimbasit.I210669//package com.ibrahimbasit.I210669
//
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SelectSessionActivity : AppCompatActivity() {

    private var monthHeader: TextView? = null
    private var calendarGrid: GridLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_session)
        monthHeader = findViewById<TextView>(R.id.month_header)
        calendarGrid = findViewById(R.id.calendar_grid)

        val backButton : View = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set the month and year

        // Set the month and year
        monthHeader?.setText("December 2023")

        // Populate the calendar grid with days

        // Populate the calendar grid with days
        for (i in 0..5) {
            for (j in 0..6) {
                val dayCell = TextView(this)
                dayCell.text = (i * 7 + j + 1).toString()

                // Highlight the current date
                if (i == 0 && j == 3) {
                    dayCell.setBackgroundColor((Color.parseColor("#FF0000")))
                }
                calendarGrid?.addView(dayCell)
            }
        }
    }
}