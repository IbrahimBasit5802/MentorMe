import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ibrahimbasit.I210669.auth.models.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class UserViewModel : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getUserProfile(email: String) {
        val requestBody = FormBody.Builder()
            .add("email", email)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.8:3000/getProfile")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UserViewModel", "Failed to fetch user data", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val user = parseUserDataFromJson(responseBody) // Parse JSON to User
                        _userData.postValue(user)
                    }
                } else {
                    Log.e("UserViewModel", "Error fetching user data: ${response.message}")
                }
            }
        })
    }

    private fun parseUserDataFromJson(json: String): User {
        return gson.fromJson(json, User::class.java)
    }
}
