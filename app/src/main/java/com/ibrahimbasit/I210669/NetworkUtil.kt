import okhttp3.*
import java.io.IOException

object NetworkUtil {
    private val client = OkHttpClient()

    fun getAllMentors(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .get() // Assume GET request if your server needs otherwise modify accordingly
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun loginUser(email: String, password: String, callback: Callback) {
        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.8:3000/authenticateUser")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(callback)
    }
}
