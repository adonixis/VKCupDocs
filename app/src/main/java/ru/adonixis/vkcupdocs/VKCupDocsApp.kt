package ru.adonixis.vkcupdocs

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import ru.adonixis.vkcupdocs.activity.WelcomeActivity

class VKCupDocsApp: Application() {
    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            WelcomeActivity.startFrom(this@VKCupDocsApp)
        }
    }
}