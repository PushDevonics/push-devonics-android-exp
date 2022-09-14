package pro.devonics.push

import android.util.Base64
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.reflect.InvocationTargetException

private const val TAG = "PushRegistratorFCM"

class PushRegistratorFCM : PushRegistratorAbstractGoogle() {

    private val FCM_DEFAULT_PROJECT_ID = "push-test-19590"
    private val FCM_DEFAULT_APP_ID = "1:910789166102:android:6b88b8bc65db02e29b6137"
    private val FCM_DEFAULT_API_KEY_BASE64 = "QUl6YVN5QzlwLVJ1WFhzMEdBX2l1eWNNakYwcTlFcXgxYXVHbnY0"
    private val FCM_APP_NAME = "Push"

    private var firebaseApp: FirebaseApp? = null
    private val appContext = AppContextKeeper.getContext()

    override fun getProviderName(): String {
        return "FCM"
    }

    override fun getToken(senderId: String): String {
        initFirebaseApp(senderId)
        //Log.d(TAG, "getToken: senderId = $senderId")

        try {
            return getTokenWithClassFirebaseMessaging()
        } catch (e: NoClassDefFoundError) {
            //Log.d(TAG, "getToken: NoClassDefFoundError = $e")
            Log.e(
                "Info",
                "FirebaseMessaging.getToken not found, attempting to use FirebaseInstanceId.getToken"
            )
        } catch (e: NoSuchMethodError) {
            //Log.d(TAG, "getToken: NoSuchMethodError = $e")
            Log.e(
                "Info",
                "FirebaseMessaging.getToken not found, attempting to use FirebaseInstanceId.getToken"
            )
        }
        return getTokenWithClassFirebaseInstanceId(senderId)
    }

    private fun getTokenWithClassFirebaseInstanceId(senderId: String): String {
        val exception: Exception
        try {
            val firebaseInstanceIdClass =
                Class.forName("com.google.firebase.iid.FirebaseInstanceId")
            val getInstanceMethod =
                firebaseInstanceIdClass.getMethod("getInstance", FirebaseApp::class.java)
            val instanceId = getInstanceMethod.invoke(null, firebaseApp)
            val getTokenMethod =
                instanceId.javaClass.getMethod("getToken", String::class.java, String::class.java)
            val token = getTokenMethod.invoke(instanceId, senderId, "FCM")
            //Log.d(TAG, "getTokenWithClassFirebaseInstanceId: token = $token")
            return token as String
        } catch (e: ClassNotFoundException) {
            exception = e
        } catch (e: NoSuchMethodException) {
            exception = e
        } catch (e: IllegalAccessException) {
            exception = e
        } catch (e: InvocationTargetException) {
            exception = e
        }

        throw Error(
            "Reflection error on FirebaseInstanceId.getInstance(firebaseApp).getToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE)",
            exception
        )
    }

    private fun getTokenWithClassFirebaseMessaging(): String {
        val fcmInstance = firebaseApp?.get(FirebaseMessaging::class.java)
        val tokenTask = fcmInstance?.token as Task<String>

        //Log.d(TAG, "getTokenWithClassFirebaseMessaging: tokenTask = $tokenTask")
        return Tasks.await(tokenTask)
    }

    private fun initFirebaseApp(senderId: String) {
        if (firebaseApp != null)
            return

        val firebaseOptions = FirebaseOptions.Builder()
            .setGcmSenderId(senderId)
            .setApplicationId(getAppId())
            .setApiKey(getApiKey())
            .setProjectId(getProjectId())
            .build()
        firebaseApp = appContext.let {
            FirebaseApp.initializeApp(
                it, firebaseOptions)
        }
    }

    private fun getAppId(): String {
        return FCM_DEFAULT_APP_ID
    }

    private fun getApiKey(): String {
        return String(Base64.decode(FCM_DEFAULT_API_KEY_BASE64, Base64.DEFAULT))
    }

    private fun getProjectId(): String {
        return FCM_DEFAULT_PROJECT_ID
    }
}