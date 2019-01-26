package com.imakeanapp.cometchat

import android.app.Application
import android.util.Log
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException

class App : Application() {

    private val appID = "160ee9f32a167"

    override fun onCreate() {
        super.onCreate()

        CometChat.init(this, appID, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(message: String) {
                Log.d("CometChat", "Initialization completed successfully: $message")
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Initialization failed with exception: ${e.message}")
            }
        })
    }
}