package pro.devonics.push.network

import android.util.Log
import pro.devonics.push.AppContextKeeper
import pro.devonics.push.PushCache
import pro.devonics.push.PushInit
import pro.devonics.push.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private const val TAG = "ApiHelper"

class ApiHelper(private val apiService: ApiService) {

    fun getSenderData(appId: String): String? {
        val call = apiService.getSenderId(appId)

        try {
            val response = call.execute()
            return response.body()?.data?.getSenderId()
            //Log.d(TAG, "getSenderData: response = $response")
        } catch (e: IOException) {
            e.printStackTrace()
            //Log.d(TAG, "getSenderData: IOException = $e")
        }
        return null
    }

    fun createPush(pushUser: PushUser, appId: String): Status? {
        val call = apiService.createPush(pushUser)
        call.enqueue(
            object : Callback<Status> {
                override fun onResponse(
                    call: Call<Status>, response: Response<Status>) {

                    val pushCache = PushCache()
                    if (response.isSuccessful) {
                        pushCache.saveSubscribeStatus(true)
                        val registrationId = pushUser.getRegistrationId()//pushCache.getRegistrationIdFromPref()
                        createSession(registrationId, appId)

                        Log.d(TAG, "createPush.onResponse: isSuccessful")
                    } else {
                        pushCache.saveSubscribeStatus(false)
                    }
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    //Log.d(TAG, "createPush.onFailure: Throwable = $t")
                }

            })
        return null
    }

    fun createSession(registrationId: String, appId: String): Status? {
        val call = apiService.createSession(registrationId)

        call.enqueue(
            object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                    val pushCache = PushCache()

                    if (response.isSuccessful) {
                        val key = pushCache.getTagKey()
                        val value = pushCache.getTagValue()
                        if (!key.equals("") && !value.equals("")) {
                            if (key != null) {
                                if (value != null) {
                                    saveTag(key, value, registrationId)
                                    pushCache.saveTagKey("")
                                    pushCache.saveTagValue("")
                                }
                            }
                        }
                        Log.d(TAG, "createSession.onResponse: isSuccessful")
                    } else {
                        val subscribe = pushCache.getSubscribeStatusFromPref()
                        val internalId = pushCache.getInternalIdFromPref()
                        Log.d(TAG, "createSession: subscribe $subscribe")
                        Log.d(TAG, "createSession: internalId $internalId")

                        if (subscribe == true && internalId != null) {
                            val pushUser = PushInit.setPushUser(
                                registrationId,
                                appId,
                                AppContextKeeper.getContext(),
                                internalId
                            )
                            createPush(pushUser, appId)

                            Log.d(TAG, "createSession: pushUser $pushUser")
                        }
                    }
                    //Log.d(TAG, "createSession.onResponse: response = $response")
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    //Log.d(TAG, "createSession.onFailure: t = $t")
                }
            }
        )
        return null
    }

    fun updateRegistrationId(registrationId: String): Status? {
        val call = apiService.updateUser(registrationId)
        call.enqueue(
            object : Callback<Status> {
                override fun onResponse(
                    call: Call<Status>, response: Response<Status>) {
                    //Log.d(TAG, "updateRegistrationId.onResponse: response = $response")
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    //Log.d(TAG, "updateRegistrationId.onFailure: t = $t")
                }

            }
        )
        return null
    }

    fun saveTag(key: String, value: String, registrationId: String): Status? {

        val tag = Tag(key, value)

        val call = apiService.saveCustomParams(registrationId, tag)

        Log.d(TAG, "saveCustomTag: tag = $tag")

        call.enqueue(
           object : Callback<Status> {
               override fun onResponse(call: Call<Status>, response: Response<Status>) {
                   //Log.d(TAG, "saveCustomTag.onResponse: response = $response")
               }

               override fun onFailure(call: Call<Status>, t: Throwable) {
                   //Log.d(TAG, "saveCustomTag.onFailure: Throwable = $t")
               }
           }
        )
        return null
    }

    fun sendTimeStatistic(registrationId: String, timeData: TimeData): Status? {
        val call = apiService.sendDuration(registrationId, timeData)
        //Log.d(TAG, "sendTimeStatistic: timeData = $timeData")
        call.enqueue(
            object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                    //Log.d(TAG, "sendTimeStatistic.onResponse: response = $response")
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    //Log.d(TAG, "sendTimeStatistic.onFailure: Throwable = $t")
                }
            }
        )
        return null
    }

    fun createTransition(registrationId: String, pushData: PushData): Status? {
        val call = apiService.createTransition(registrationId, pushData)
        //Log.d(TAG, "createTransition: registrationId = $registrationId")
        call.enqueue(
            object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    //Log.d(TAG, "createTransition.onFailure: Throwable = $t")
                }
            }
        )
        return null
    }
}