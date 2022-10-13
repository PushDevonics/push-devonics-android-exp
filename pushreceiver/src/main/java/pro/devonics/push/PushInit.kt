package pro.devonics.push

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import pro.devonics.push.model.PushUser
import pro.devonics.push.network.ApiHelper
import java.util.*

private const val TAG = "PushInit"

class PushInit {

    companion object {
        private val FCM_DEFAULT_PROJECT_ID = "push-test-19590"
        private val FCM_DEFAULT_APP_ID = "1:910789166102:android:6b88b8bc65db02e29b6137"
        private val FCM_DEFAULT_API_KEY_BASE64 = "QUl6YVN5QzlwLVJ1WFhzMEdBX2l1eWNNakYwcTlFcXgxYXVHbnY0"

        private var firebaseApp: FirebaseApp? = null
        private val appContext = AppContextKeeper.getContext()
        private val pushCache = PushCache()

        fun run(appId: String, service: ApiHelper) {
            val thread = Thread {
                val sender = service.getSenderData(appId)
                if (sender != null) {
                    initFirebaseApp(sender)
                    try {
                        val registrationId = getToken()
                        //val regId = pushCache.getRegistrationIdFromPref()
                        val internalId = pushCache.getInternalIdFromPref()
                        val status = pushCache.getSubscribeStatusFromPref()
                        if (registrationId != null) {
                            pushCache.saveRegistrationIdPref(registrationId)
                            if (status == false) {
                                val pushUser = internalId?.let {
                                    setPushUser(
                                        registrationId, appId, appContext, it
                                    )
                                }
                                pushUser?.let { service.createPush(it, appId) }
                            }
                        }
                        Log.d(TAG, "Device registered, push token = $registrationId")
                    } catch (e: NoClassDefFoundError) {
                        Log.e(
                            "Info",
                            "FirebaseMessaging.getToken not found, attempting to use FirebaseInstanceId.getToken"
                        )
                    } catch (e: NoSuchMethodError) {
                        Log.e(
                            "Info",
                            "FirebaseMessaging.getToken not found, attempting to use FirebaseInstanceId.getToken"
                        )
                    }
                }
            }
            thread.start()
        }

        private fun initFirebaseApp(senderId: String) {
            if (firebaseApp != null) {
                return
            }

            val firebaseOptions = FirebaseOptions.Builder()
                .setGcmSenderId(senderId)
                .setApplicationId(FCM_DEFAULT_APP_ID)
                .setApiKey(String(Base64.decode(FCM_DEFAULT_API_KEY_BASE64, Base64.DEFAULT)))
                .setProjectId(FCM_DEFAULT_PROJECT_ID)
                .build()
            firebaseApp = appContext.let {
                FirebaseApp.initializeApp(
                    it, firebaseOptions
                )
            }
        }

        private fun getToken(): String {
            val fcmInstance = firebaseApp?.get(FirebaseMessaging::class.java)
            val tokenTask = fcmInstance?.token as Task<String>
            return Tasks.await(tokenTask)
        }

        fun setPushUser(
            registrationId: String,
            appId: String,
            appContext: Context,
            internalId: String): PushUser {

            //Get timezone
            val tz = TimeZone.getDefault()//.toZoneId()
            val timezone = tz.id
            //Log.d(TAG, "complete: timezone = $timezone")

            //Get language
            val locale = Locale.getDefault()
            val lang = locale.language
            //Log.d(TAG, "complete: lang = $lang")

            //Get country
            val telephonyManager = appContext
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val country = telephonyManager.simCountryIso.uppercase(Locale.getDefault())
            //val country = Locale("", locale.country).country
            //.getDisplayCountry(Locale("EN"))
            //Log.d(TAG, "complete: country = $country")


            //Get device info
            val deviceInfo = getDeviceData()
            //Log.d(TAG, "complete: deviceInfo = $deviceInfo")

            return PushUser(
                registrationId,
                internalId,
                appId,
                1,
                country,
                lang,
                timezone,
                deviceInfo
            )
        }

        private fun getDeviceData(): String {
            //val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val brand = Build.BRAND
            //val product = Build.PRODUCT

            return "$brand/$model"
        }
    }
}