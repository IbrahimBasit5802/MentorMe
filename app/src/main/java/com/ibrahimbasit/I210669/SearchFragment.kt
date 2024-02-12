package com.ibrahimbasit.I210669

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)

        val mentorText : TextView = view.findViewById(R.id.recent_text1)

        val mentorText2 : TextView = view.findViewById(R.id.recent_text2)

        val mentorText3 : TextView = view.findViewById(R.id.recent_text3)

        val backButton : View = view.findViewById(R.id.back_arrow)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        mentorText.setOnClickListener {
            performSearch(mentorText.text.toString())
        }

        mentorText2.setOnClickListener {
            performSearch(mentorText2.text.toString())
        }

        mentorText3.setOnClickListener {
            performSearch(mentorText3.text.toString())
        }


        // Trigger search on action done (for example, clicking the keyboard search button)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

    }

    private fun performSearch(query: String) {
        // Create SearchResultFragment and pass the search query as an argument
        val searchResultFragment = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putString("query", query)
            }
        }

        // Replace the current fragment with SearchResultFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, searchResultFragment) // Replace 'fragment_container' with your actual container ID
            .addToBackStack(null) // Add this transaction to the back stack
            .commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}