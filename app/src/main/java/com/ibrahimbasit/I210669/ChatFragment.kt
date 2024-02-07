package com.ibrahimbasit.I210669

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val clickListener = View.OnClickListener { clickedView ->
            val tag = clickedView.tag.toString()


            val chatPersonFragment = ChatPersonFragment.newInstance(tag, "param2")
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, chatPersonFragment)
                ?.addToBackStack(null)
                ?.commit()
        }


        val clickListener2 = View.OnClickListener { clickedView ->
            val tag = clickedView.tag.toString()


            val communityChatFragment = CommunityChatFragment.newInstance(tag, "param2")
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, communityChatFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        val scrollViewLayout = view.findViewById<LinearLayout>(R.id.chatList)

        val communityScrollView = view.findViewById<LinearLayout>(R.id.communityList)

        for (i in 0 until scrollViewLayout.childCount) {
            val child = scrollViewLayout.getChildAt(i)
            child.setOnClickListener(clickListener)
        }

        for (i in 0 until communityScrollView.childCount) {
            val child = communityScrollView.getChildAt(i)
            child.setOnClickListener(clickListener2)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}