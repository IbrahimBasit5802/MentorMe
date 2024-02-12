package com.ibrahimbasit.I210669

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibrahimbasit.I210669.adapters.SearchResultAdapter
import com.ibrahimbasit.I210669.data.SearchResultItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SearchResultAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val myDataset = listOf(
        SearchResultItem("Sample 1", "Lead - Technology Officer", true, "$500/Session", true, "Available"),
        SearchResultItem("Sample 2", "Lead - Technology Officer", false, "$500/Session", false, "Not Available"),
        SearchResultItem("Sample 3", "Lead - Technology Officer", false, "$500/Session", false, "Not Available"),
        SearchResultItem("Sample 4", "Lead - Technology Officer", true, "$500/Session", true, "Available"),
        SearchResultItem("Sample 5", "Lead - Technology Officer", true, "$500/Session", true, "Available")
    )

    // Initialize with empty list or actual data

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
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backArrow = view.findViewById<View>(R.id.backButton)
        backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val clickListener = View.OnClickListener {
            val intent = Intent(activity, BookSessionActivity::class.java)
            startActivity(intent)

        }
        viewManager = LinearLayoutManager(context)
        viewAdapter = SearchResultAdapter(myDataset, clickListener)

        recyclerView = view.findViewById<RecyclerView>(R.id.search_results_recyclerview).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }



        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            child.setOnClickListener(clickListener)
        }

    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}