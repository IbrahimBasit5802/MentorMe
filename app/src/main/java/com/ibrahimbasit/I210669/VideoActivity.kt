package com.ibrahimbasit.I210669

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TextView

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        val closeButton : View = findViewById<View>(R.id.closeButton)

        // Get references to your TextViews here.
        val textViewSloMo = findViewById<TextView>(R.id.textViewSloMo)
        val textViewVideo = findViewById<TextView>(R.id.textViewVideo)
        val textViewPhoto = findViewById<TextView>(R.id.textViewPhoto)
        val textViewPortrait = findViewById<TextView>(R.id.portrait)
        val textViewSquare = findViewById<TextView>(R.id.textViewSquare)

        val cameraButton : View = findViewById(R.id.cameraButton)
        cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        // ... other text views

        // Array of all TextViews for easier iteration
        val allTextViews = arrayOf(textViewSloMo, textViewVideo, textViewPhoto, textViewSquare, textViewPortrait)

        // Reference to the HorizontalScrollView
        val scrollView = findViewById<HorizontalScrollView>(R.id.options)


        allTextViews[1].isSelected = true

        allTextViews.forEach { textView ->
            textView.setOnClickListener {
                allTextViews.forEach { it.isSelected = false }

                // Select the clicked one
                textView.isSelected = true

                // This will get the coordinates of the TextView's rect and its parent (the LinearLayout)
                val rect = Rect()
                textView.getHitRect(rect)

                // Calculate the scroll amount: the left edge of the selected TextView minus half the width of the screen
                val scrollX = rect.left - (scrollView.width / 2 - textView.width / 2)

                // Scroll to the calculated position
                scrollView.smoothScrollTo(scrollX, 0)
            }
        }

        allTextViews.forEach { textView ->
            textView.setOnClickListener {
                // Update selection for all TextViews
                allTextViews.forEach {
                    it.isSelected = false
                    it.setBackgroundResource(R.drawable.selector_background)
                    it.setTextColor(resources.getColorStateList(R.color.selector_text_color))
                }

                // Select the clicked one
                textView.isSelected = true

                // Center the clicked TextView in the HorizontalScrollView
                centerTextViewInScrollView(textView)
            }
        }


        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun centerTextViewInScrollView(textView: TextView) {
        val scrollView = findViewById<HorizontalScrollView>(R.id.options)
        val rect = Rect()
        textView.getHitRect(rect)
        val scrollX = rect.left - (scrollView.width / 2 - textView.width / 2)
        scrollView.smoothScrollTo(scrollX, 0)
    }
}
