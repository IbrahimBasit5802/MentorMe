import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ibrahimbasit.I210669.Mentor

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<List<Mentor>>()
    val searchResults: LiveData<List<Mentor>> = _searchResults

    fun performSearch(query: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Mentors")
        databaseReference.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchResults = mutableListOf<Mentor>()
                    for (mentorSnapshot in snapshot.children) {
                        val mentor = mentorSnapshot.getValue(Mentor::class.java)
                        mentor?.let { searchResults.add(it) }
                    }
                    _searchResults.postValue(searchResults)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                }
            })
    }
}
