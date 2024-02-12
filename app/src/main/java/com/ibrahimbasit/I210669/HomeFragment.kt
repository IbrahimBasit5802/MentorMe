package com.ibrahimbasit.I210669

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var buttonsList: List<Button>
    private var selectedButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize your buttons
        val allButton = view.findViewById<Button>(R.id.all_button)
        val educationButton = view.findViewById<Button>(R.id.education_button)
        val entreButton = view.findViewById<Button>(R.id.entrepreneurship_button)
        val personalGrowthButton = view.findViewById<Button>(R.id.personal_growth_button)
        val notificationButton : View = view.findViewById(R.id.notification_button)

        notificationButton.setOnClickListener {
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }

        setSelectedButton(allButton)
        // ... Initialize other buttons ...

        // Add all buttons to a list
        buttonsList = listOf(allButton, educationButton, entreButton, personalGrowthButton)

        // Set up click listeners
        buttonsList.forEach { button ->
            button.setOnClickListener { clickedButton ->
                setSelectedButton(clickedButton as Button)
            }
        }
        val twelveDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f,
            context?.getResources()?.getDisplayMetrics() ?: resources.displayMetrics
        )

        val radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4f,
            context?.getResources()?.getDisplayMetrics() ?: resources.displayMetrics
        )

        val shapeDrawable = ShapeDrawable()
        shapeDrawable.paint.color = Color.parseColor("#d5d5d5")
        shapeDrawable.paint.setShadowLayer(
            radius, //blur
            radius, //dx
            radius, //dy
            Color.parseColor("#00000040") //color
        )
        val outerRadius = floatArrayOf(
            twelveDp, twelveDp, //top-left
            twelveDp, twelveDp, //top-right
            twelveDp, twelveDp, //bottom-right
            twelveDp, twelveDp  //bottom-left
        )
        shapeDrawable.shape = RoundRectShape(outerRadius, null, null)
        view.findViewById<View>(R.id.mentorbox).background = shapeDrawable
        view.findViewById<View>(R.id.mentorbox2).background = shapeDrawable
        view.findViewById<View>(R.id.mentorbox3).background = shapeDrawable

        val clickListener = View.OnClickListener {clickedView ->
            val intent = Intent(activity, BookSessionActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.mentorbox).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.mentorbox2).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.mentorbox3).setOnClickListener(clickListener)


        view.findViewById<View>(R.id.education_box).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.education_box2).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.education_box3).setOnClickListener(clickListener)

        view.findViewById<View>(R.id.recent_mentor_box1).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.recent_mentor_box2).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.recent_mentor_box3).setOnClickListener(clickListener)


        return view
    }

    private fun setSelectedButton(button: Button) {
        // Change background and text color of the clicked button
        selectedButton?.let {
            it.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.chipColor)
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.headingTextColor))
        }

        button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.headingTextColor)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryTextColor))

        // Keep reference to the selected button
        selectedButton = button
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}