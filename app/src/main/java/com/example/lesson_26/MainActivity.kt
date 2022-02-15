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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import okhttp3.*
import java.io.IOException
import java.security.MessageDigest
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {

    lateinit var requestViewModel: RequestViewModel
    var edName: EditText? = null
    var edPassword: EditText? = null
    var btnLogin: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestViewModel = ViewModelProvider(this).get(RequestViewModel::class.java)
        edName = findViewById(R.id.edName)
        edPassword = findViewById(R.id.edPassword)
        btnLogin = findViewById(R.id.btnLogin)

        val intent = Intent(this@MainActivity, LastFM::class.java)
        var preferences = getSharedPreferences("MyPreference", MODE_PRIVATE)
        if (preferences.contains("name")) {
            edName?.setText(preferences.getString("name", "Default"))
            edPassword?.setText(preferences.getString("password", "Default"))
        }

        requestViewModel.liveDataBoolean.observe(
            this,
            Observer {
                if (it == true) {
                    Toast.makeText(
                        this,
                        "Продолжай работать!",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Неверное имя или пароль !!!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        btnLogin?.setOnClickListener(View.OnClickListener {
            val username = edName?.text.toString()
            val userpassword = edPassword?.text.toString()

            requestViewModel.request(userpassword, username)
        })
    }
}

