package com.imakeanapp.cometchat

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage

class MessagesActivity : AppCompatActivity() {

    private lateinit var enterMessage: EditText
    private lateinit var send: Button
    private lateinit var messages: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter

    private var isLoggingOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        enterMessage = findViewById(R.id.enter_message)
        send = findViewById(R.id.send_message)
        messages = findViewById(R.id.messages)
        messages.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter(CometChat.getLoggedInUser().uid, listOf())
        messages.adapter = messagesAdapter

        send.setOnClickListener {
            sendMessage()
        }

        joinGroup()
        fetchMessages()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                logout()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun joinGroup() {
        CometChat.joinGroup(
            "androidroom",
            CometChatConstants.GROUP_TYPE_PUBLIC,
            "",
            object : CometChat.CallbackListener<String>() {
                override fun onSuccess(successMessage: String) {
                    Log.d("CometChat", "Group joined successfully")
                }

                override fun onError(e: CometChatException) {
                    Log.d("CometChat", "Group joining failed with exception ${e.message}")
                }
            })
    }

    private fun sendMessage() {
        val textMessage = TextMessage(
            "androidroom",
            enterMessage.text.toString(),
            CometChatConstants.MESSAGE_TYPE_TEXT,
            CometChatConstants.RECEIVER_TYPE_GROUP
        )

        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(message: TextMessage) {
                enterMessage.setText("")
                Log.d("CometChat", "Message sent successfully: $message")
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Message sending failed with exception: ${e.message}")
            }
        })
    }

    private fun fetchMessages() {
        val messagesRequest = MessagesRequest.MessagesRequestBuilder()
            .setGUID("androidroom")
            .setLimit(30)
            .build()

        messagesRequest.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(messages: List<BaseMessage>) {
                Log.d("CometChat", "Messages received ${messages.joinToString()}")
                messagesAdapter.updateMessages(messages)
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Message sending failed with exception: ${e.message}")
            }
        })
    }

    private fun logout() {
        if (!isLoggingOut) {
            logoutFromCometChat(
                { runOnUiThread { super@MessagesActivity.onBackPressed() } },
                { runOnUiThread { isLoggingOut = false } }
            )
        }
    }

    private fun logoutFromCometChat(success: () -> Unit, failed: () -> Unit) {
        CometChat.logout(object : CometChat.CallbackListener<String>() {
            override fun onSuccess(successMessage: String) {
                Log.d("CometChat", "Logout successful $successMessage")
                success.invoke()
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Logout failed with exception: ${e.message}")
                failed.invoke()
            }
        })
    }

    override fun onBackPressed() {
        logout()
    }
}
