package com.ibrahimbasit.I210669//package com.ibrahimbasit.I210669
//
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class SelectSessionActivity : AppCompatActivity() {

    private var monthHeader: TextView? = null
    private var calendarGrid: GridLayout? = null
    private lateinit var buttonsList: List<Button>
    private var selectedButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_session)

        val backButton : View = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val timeSlotButton : Button = findViewById(R.id.timeSlot1)
        val timeSlotButton2 : Button = findViewById(R.id.timeSlot2)
        val timeSlotButton3 : Button = findViewById(R.id.timeSlot3)
        val timeSlotButton4 : Button = findViewById(R.id.timeSlot4)


        setSelectedButton(timeSlotButton2)
        buttonsList = (listOf(timeSlotButton, timeSlotButton2, timeSlotButton3, timeSlotButton4))
        buttonsList.forEach { button ->
            button.setOnClickListener { clickedButton ->
                setSelectedButton(clickedButton as Button)
            }
        }

        val chatButton : Button = findViewById(R.id.chatButton)
        chatButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                // Add extra to indicate the target fragment
                putExtra("navigateTo", "ChatPersonFragment")
            }
            startActivity(intent)
        }

        val callButton : Button = findViewById(R.id.phoneButton)

        callButton.setOnClickListener {
            val intent = Intent(this, CallScreenActivity::class.java)

            startActivity(intent)
        }

        val videoButton : Button = findViewById(R.id.videoButton)

        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)

            startActivity(intent)
        }

        val bookButton : Button = findViewById(R.id.bookSessionButton)
        bookButton.setOnClickListener {
            finish()
        }




    }

    private fun setSelectedButton(button: Button) {
        // Change background and text color of the clicked button
        selectedButton?.let {
            it.backgroundTintList = ContextCompat.getColorStateList(this, R.color.timeSloeUnselectedColor)
            it.setTextColor(ContextCompat.getColor(this, R.color.secondaryTextColor))
        }

        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.timeSlotColor)
        button.setTextColor(ContextCompat.getColor(this, R.color.primaryTextColor))

        // Keep reference to the selected button
        selectedButton = button
    }
}