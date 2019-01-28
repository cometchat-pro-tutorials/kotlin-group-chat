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
    private lateinit var messagesList: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter

    private val listenerID = "MESSAGES_LISTENER"
    private val roomID = "androidroom"
    private var isLoggingOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        enterMessage = findViewById(R.id.enter_message)
        send = findViewById(R.id.send_message)

        messagesList = findViewById(R.id.messages)
        val layoutMgr = LinearLayoutManager(this)
        layoutMgr.stackFromEnd = true
        messagesList.layoutManager = layoutMgr

        messagesAdapter = MessagesAdapter(CometChat.getLoggedInUser().uid, mutableListOf())
        messagesList.adapter = messagesAdapter

        send.setOnClickListener {
            sendMessage()
        }

        joinGroup()
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

    override fun onResume() {
        super.onResume()
        CometChat.addMessageListener(listenerID, object : CometChat.MessageListener() {
            override fun onTextMessageReceived(message: TextMessage) {
                messagesAdapter.appendMessage(message)
                scrollToBottom()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        CometChat.removeMessageListener(listenerID)
    }

    private fun joinGroup() {
        CometChat.joinGroup(
            roomID,
            CometChatConstants.GROUP_TYPE_PUBLIC,
            "",
            object : CometChat.CallbackListener<String>() {
                override fun onSuccess(successMessage: String) {
                    fetchMessages()
                }

                override fun onError(e: CometChatException) {
                    e.code?.let {
                        // For now, we'll just keep on attempting to join the group
                        // because persistence is out of the scope for this tutorial
                        if (it.contentEquals("ERR_ALREADY_JOINED")) {
                            fetchMessages()
                        }
                    }
                }
            })
    }

    private fun sendMessage() {
        val textMessage = TextMessage(
            roomID,
            enterMessage.text.toString(),
            CometChatConstants.MESSAGE_TYPE_TEXT,
            CometChatConstants.RECEIVER_TYPE_GROUP
        )

        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(message: TextMessage) {
                enterMessage.setText("")
                messagesAdapter.appendMessage(message)
                scrollToBottom()
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Message send failed: ${e.message}")
            }
        })
    }

    private fun fetchMessages() {
        val messagesRequest = MessagesRequest.MessagesRequestBuilder()
            .setGUID(roomID)
            .setLimit(30)
            .build()

        messagesRequest.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(messages: List<BaseMessage>) {
                messagesAdapter.updateMessages(messages)
                scrollToBottom()
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Fetch messages failed: ${e.message}")
            }
        })
    }

    private fun scrollToBottom() {
        messagesList.scrollToPosition(messagesAdapter.itemCount - 1)
    }

    override fun onBackPressed() {
        logout()
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
            override fun onSuccess(successMessage: String) { success.invoke() }
            override fun onError(e: CometChatException) { failed.invoke() }
        })
    }
}
