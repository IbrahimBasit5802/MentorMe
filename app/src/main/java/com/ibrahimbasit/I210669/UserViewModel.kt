import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.auth.models.User

class UserViewModel : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData
    private val dbRef = Firebase.database.reference

    init {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { userId ->
            val userRef = dbRef.child("Users").child(userId)

            // Fetch the initial user data
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _userData.value = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })

            // Listen for changes in the user data
            userRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    println("here we are")
                    Log.d("UserViewModel", "onChildChanged snapshot: ${snapshot.value}")
                    val currentUserData = _userData.value ?: User()
                    when (snapshot.key) {
                        "name" -> currentUserData.name = snapshot.getValue(String::class.java) ?: ""
                        "email" -> currentUserData.email = snapshot.getValue(String::class.java) ?: ""
                        "contactNumber" -> currentUserData.contactNumber = snapshot.getValue(String::class.java) ?: ""
                        "country" -> currentUserData.country = snapshot.getValue(String::class.java) ?: ""
                        "city" -> currentUserData.city = snapshot.getValue(String::class.java) ?: ""
                        "profilePictureUrl" -> currentUserData.profilePictureUrl = snapshot.getValue(String::class.java) ?: ""

                    }
                    _userData.value = currentUserData

                    println("success ig")
                }



                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Handle if needed
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle if needed
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }

}

