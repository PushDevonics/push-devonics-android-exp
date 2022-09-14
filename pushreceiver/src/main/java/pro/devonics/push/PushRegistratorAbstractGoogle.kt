package pro.devonics.push

import android.content.Context
import android.util.Log
import java.io.IOException

private const val TAG = "PushRegistratorAbstract"

abstract class PushRegistratorAbstractGoogle : PushRegistrator {

    private var registeredHandler: PushRegistrator.RegisteredHandler? = null
    //private val registeredHandler = PushRegistrator.RegisteredHandler

    private val REGISTRATION_RETRY_COUNT = 5
    private val REGISTRATION_RETRY_BACKOFF_MS = 10000

    abstract fun getProviderName(): String
    abstract fun getToken(senderId: String): String

    override fun registerForPush(
        context: Context,
        senderId: String,
        callback: PushRegistrator.RegisteredHandler
    ) {
        registeredHandler = callback

        if (isValidProjectNumber(senderId, callback))
            internalRegisterForPush(senderId)
        //Log.d(TAG, "registerForPush: senderId = $senderId")
    }

    private fun internalRegisterForPush(senderId: String) {
        try {
            registerInBackground(senderId)
            //Log.d(TAG, "internalRegisterForPush: senderId = $senderId")
        } catch (t: Throwable) {
            //Log.d(TAG, "registerForPush: Throwable = $t")
            /*Log.e("Error", "Could not register with ${getProviderName()}"
                //+ getProviderName() +
                + " due to an issue with your AndroidManifest.xml or with 'Google Play services'.  $t")*/
            registeredHandler?.complete(null, UserState.PUSH_STATUS_FIREBASE_FCM_INIT_ERROR)
        }
    }

    private var registerThread: Thread? = null

    @Synchronized
    private fun registerInBackground(senderId: String) {
        if (registerThread != null && registerThread!!.isAlive)
            return

        registerThread = Thread(Runnable {
            for (currentRetry in 0 until REGISTRATION_RETRY_COUNT) {
                val finished: Boolean = attemptRegistration(senderId, currentRetry)
                if (finished) {
                    return@Runnable
                }
                OSUtils.sleep(REGISTRATION_RETRY_BACKOFF_MS * (currentRetry + 1))
            }
        })
        registerThread!!.start()
    }

    private var firedCallback: Boolean = false
    private fun attemptRegistration(senderId: String, currentRetry: Int): Boolean {
        try {
            val registrationId = getToken(senderId)

            Log.d(TAG, "Device registered, push token = $registrationId")
            registeredHandler?.complete(registrationId, UserState.PUSH_STATUS_SUBSCRIBED)
            return true
        } catch (e: IOException) {
            if ("SERVICE_NOT_AVAILABLE" != e.message) {
                Log.e("Error", "Error Getting ${getProviderName()}  Token $e")
                if (!firedCallback)
                    registeredHandler?.complete(
                        null, UserState.PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION
                    )
                return true
            } else {
                if (currentRetry >= (REGISTRATION_RETRY_COUNT - 1))
                    Log.e("Error",
                        "Retry count of $REGISTRATION_RETRY_COUNT exceed! " +
                                "Could not get a ${getProviderName()} Token. $e")
                else {
                    Log.e("Error",
                        "'Google Play services' returned SERVICE_NOT_AVAILABLE error." +
                                " Current retry count: $currentRetry, $e")
                    if (currentRetry == 2) {
                        registeredHandler?.complete(null,
                            UserState.PUSH_STATUS_FIREBASE_FCM_ERROR_SERVICE_NOT_AVAILABLE
                        )
                        firedCallback = true
                        return true
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e("Error",
                "Unknown error getting ${getProviderName()} Token $t")
            registeredHandler?.complete(
                null, UserState.PUSH_STATUS_FIREBASE_FCM_ERROR_MISC_EXCEPTION
            )
            return true
        }
        return false
    }

    private fun isValidProjectNumber(senderId: String, callback: PushRegistrator.RegisteredHandler): Boolean {
        var isProjectNumberValidFormat: Boolean
        try {
            java.lang.Float.parseFloat(senderId)
            isProjectNumberValidFormat = true
        } catch (t: Throwable) {
            isProjectNumberValidFormat = false
        }

        if (!isProjectNumberValidFormat) {
            Log.e(
                "Error",
                "Missing Google Project number!\nPlease enter a Google Project number" +
                        " / Sender ID on under Sender Settings > Android > " +
                        "Configuration on the OneSignal dashboard."
            )
            callback.complete(null, UserState.PUSH_STATUS_INVALID_FCM_SENDER_ID)
            return false
        }
        return true
    }
}