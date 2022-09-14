package pro.devonics.push

import android.content.Context

interface PushRegistrator {

    interface RegisteredHandler {

        fun complete(registrationId: String?, status: Int)
    }

    fun registerForPush(
        context: Context, senderId: String, callback: RegisteredHandler
    )
}