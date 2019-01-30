package com.imakeanapp.cometchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User

class MainActivity : AppCompatActivity() {

    private lateinit var join: Button
    private lateinit var username: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.username)

        join = findViewById(R.id.join_chat)
        join.setOnClickListener {
            disableAuthField()
            login()
        }
    }

    private fun disableAuthField() {
        join.isEnabled = false
        username.isEnabled = false
    }

    private fun login() {
        CometChat.login(username.text.toString(), getString(R.string.apiKey), object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User) {
                username.setText("")

                enableAuthField()

                val intent = Intent(this@MainActivity, MessagesActivity::class.java)
                startActivity(intent)
            }

            override fun onError(e: CometChatException) {
                Toast.makeText(this@MainActivity, "Error or username doesn't exist.", Toast.LENGTH_SHORT).show()
                enableAuthField()
            }
        })
    }

    private fun enableAuthField() {
        join.isEnabled = true
        username.isEnabled = true
    }
}
