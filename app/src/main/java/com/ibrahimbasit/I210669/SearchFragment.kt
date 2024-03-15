package com.ibrahimbasit.I210669

import SearchViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    private lateinit var viewModel: SearchViewModel


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
        viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)


        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

//        mentorText.setOnClickListener {
//            performSearch(mentorText.text.toString())
//        }
//
//        mentorText2.setOnClickListener {
//            performSearch(mentorText2.text.toString())
//        }
//
//        mentorText3.setOnClickListener {
//            performSearch(mentorText3.text.toString())
//        }


        // Trigger search on action done (for example, clicking the keyboard search button)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.performSearch(searchEditText.text.toString())
                openSearchResultFragment()
                true
            } else {
                false
            }
        }

    }

    private fun openSearchResultFragment() {
        val searchResultFragment = SearchResultFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, searchResultFragment)
            .addToBackStack(null)
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