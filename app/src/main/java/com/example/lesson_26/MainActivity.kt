package com.example.lesson_26

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import java.io.IOException
import java.security.MessageDigest
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {
    var edName: EditText? = null
    var edPassword: EditText? = null
    var test: String? = null
    var str = ""
    var isSuccess = true
    var btnLogin: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edName = findViewById(R.id.edName)
        edPassword = findViewById(R.id.edPassword)
        btnLogin = findViewById(R.id.btnLogin)

        val api_key = "12ff2c46a365c33acdaa0ad7291e3276"
        val api_sig = "1e7bc4f519cf557282058fa796127b6f"
        btnLogin?.setOnClickListener(View.OnClickListener {
            val username =
                edName?.text.toString()
            val password = edPassword?.text.toString()
            val apiSignature =
                "api_key" + api_key + "methodauth.getMobileSessionpassword" + password + "username" + username + api_sig
            val hexString = StringBuilder()
            Thread {
                try {
                    val md5Encrypter = MessageDigest.getInstance("MD5")

                    val digest = MessageDigest
                        .getInstance("MD5")
                    digest.update(apiSignature.toByteArray(charset("UTF-8")))
                    val messageDigest = digest.digest()


                    for (aMessageDigest in messageDigest) {
                        var hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
                        while (hex.length < 2) hex = "0$hex"
                        hexString.append(hex)
                    }
                    val urlParameter =
                        "method=auth.getMobileSession&api_key=$api_key&password=$password&username=$username&api_sig=$hexString"
                    val request = okhttp3.Request.Builder()
                        .url("https://ws.audioscrobbler.com/2.0/?$urlParameter")
                        .post(RequestBody.create(null, ByteArray(0))).build()
                    val client = OkHttpClient.Builder().build()
                    client.newCall(request).enqueue(object : Callback, okhttp3.Callback {


                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                            e.printStackTrace()
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                            response.body().use { responseBody ->
                                var test = responseBody!!.string()
                                Log.d(
                                    "MyLog",
                                    test
                                )
                                if (test.contains("ok")) {
                                    val intent =
                                        Intent(this@MainActivity, LastFM::class.java)
                                    startActivity(intent)
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Нет збоя",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Неверное имя или пароль !!!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            }
                        }
                    })
                } catch (ex: Exception) {
                    println(ex.toString())
                }
            }.start()
        })
    }
}