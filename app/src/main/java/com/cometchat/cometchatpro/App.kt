package com.cometchat.cometchatpro

import android.app.Application
import android.util.Log
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val appSettings =AppSettings.AppSettingsBuilder()
                .setRegion(getString(R.string.region))
                .subscribePresenceForAllUsers().build();

        CometChat.init(this, getString(R.string.appID),appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(message: String) {
                Log.d("CometChat", "Initialization completed: $message")
            }

            override fun onError(e: CometChatException) {
                Log.d("CometChat", "Initialization failed: ${e.message}")
            }
        })
    }
}