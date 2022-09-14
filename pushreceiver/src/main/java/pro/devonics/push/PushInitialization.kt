package pro.devonics.push

import android.content.Context
import android.os.Build
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import pro.devonics.push.network.RetrofitBuilder
import pro.devonics.push.model.PushInstance
import pro.devonics.push.model.PushUser
import pro.devonics.push.network.ApiHelper
import java.util.*

private const val TAG = "PushInitialization"

class PushInitialization {

    companion object {

        fun run(appId: String) {

            val appContext = AppContextKeeper.getContext()
            val service = ApiHelper(RetrofitBuilder.apiService)

            val thread = Thread {

                val sender = service.getSenderData(appId)
                //Log.d(TAG, "run: senderId = ${sender?.getSenderId()}")
                val pushRegistratorFCM = PushRegistratorFCM()
                if (sender != null) {
                    pushRegistratorFCM.registerForPush(
                        appContext,
                        sender,
                        object : PushRegistrator.RegisteredHandler {
                            override fun complete(registrationId: String?, status: Int) {
                                //Log.d(TAG, "complete: registrationId = $registrationId}")
                                //Log.d(TAG, "complete: status = $status}")
                                val pushCache = PushCache()
                                val regId = pushCache.getRegistrationIdFromPref()
                                val internalId = pushCache.getInternalIdFromPref()

                                if (registrationId != null) {
                                    pushCache.saveRegistrationIdPref(registrationId)
                                    if (regId == null) {
                                        val pushUser = internalId?.let {
                                            setPushUser(registrationId, appId, appContext, it)
                                        }
                                        val subscribe = pushUser?.let { service.createPush(it) }

                                        //Log.d(TAG, "complete: subscribe = $subscribe")
                                        //Log.d(TAG, "complete: pushUser = $pushUser")
                                    }
                                }




                                if (status > 0) {
                                    pushCache.saveRegistrationStatus(status)
                                }
                                //val session = registrationId?.let { service.createSession(it) }
                                //Log.d(TAG, "complete: session = $session")
                            }
                        }
                    )
                }
            }
            thread.start()
        }

        private fun setPushUser(
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