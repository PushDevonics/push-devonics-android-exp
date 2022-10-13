package pro.devonics.push

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import pro.devonics.push.DataHelper.Companion.startTime
import pro.devonics.push.model.PushData
import pro.devonics.push.model.TimeData
import pro.devonics.push.network.ApiHelper
import pro.devonics.push.network.RetrofitBuilder
import java.util.*


private const val TAG = "PushDevonics"
private const val PERMISSIONS_REQUEST_CODE = 2

class PushDevonics(activity: Activity, appId: String)
    : LifecycleEventObserver, Application.ActivityLifecycleCallbacks {

    private val service = ApiHelper(RetrofitBuilder.apiService)
    private val helperCache = HelperCache(activity)
    private val myContext = activity
    private var sentPushId: String? = null

    init {
        AppContextKeeper.setContext(activity)
        PushInit.run(appId, service)
        startTime()
        startSession(appId)
        createInternalId()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(TAG, "onStateChanged: source = $source")
        when (event) {
            Lifecycle.Event.ON_CREATE -> askNotificationPermission()
            Lifecycle.Event.ON_START -> Log.d(TAG, "ON_START: ")
            Lifecycle.Event.ON_RESUME -> {
                sendTransition(service)
                openUrl()
            }
            Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "ON_PAUSE: ")//openUrl(myContext)
            Lifecycle.Event.ON_STOP -> stopSession()//Log.d(TAG, "onStop: ")
            Lifecycle.Event.ON_DESTROY -> Log.d(TAG, "ON_DESTROY: ")
            else -> {}
        }
    }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(myContext, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    myContext,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                Log.v(TAG, "askNotificationPermission: ")
            } else {
                myContext.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun sendTransition(service: ApiHelper) {
        sentPushId = helperCache.getSentPushId()
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationIdFromPref()
        val transition = helperCache.getTransitionSt()
        if (transition == false && sentPushId != null) {
            val pushData = sentPushId?.let { PushData(it) }
            if (pushData != null && registrationId != null) {
                service.createTransition(registrationId, pushData)
            }
        }
        helperCache.saveSentPushId(null)
    }

    private fun openUrl() {
        val openUrl = helperCache.getOpenUrl()

        if (openUrl != null) {
            val urlIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(openUrl))

            urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                myContext.startActivity(urlIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "ActivityNotFoundException $e")
            }
        }
        helperCache.saveOpenUrl(null)
        //Log.d(TAG, "openUrl = $openUrl")
    }

    fun getDeeplink(): String {
        val deep1 = helperCache.getDeeplink()
        helperCache.saveDeeplink("")
        return deep1.toString()
    }

    private fun createInternalId() {
        val pushCache = PushCache()

        var internalId = pushCache.getInternalIdFromPref()
        if (internalId == null) {
            internalId = UUID.randomUUID().toString()
            pushCache.saveInternalId(internalId)
        }
    }

    fun getInternalId(): String? {
        val pushCache = PushCache()
        return pushCache.getInternalIdFromPref()
    }

    private fun startSession(appId: String) {
        Log.d(TAG, "startSession: ")
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationIdFromPref()
        if (pushCache.getSubscribeStatusFromPref() == true) {
            val session = registrationId?.let { service.createSession(it, appId) }
            //Log.d(TAG, "subscribeStatus = ${pushCache.getSubscribeStatusFromPref()}")

        }
    }

    private fun stopSession() {
        val duration = DataHelper.getDuration()
        val pushCache = PushCache()
        val regId = pushCache.getRegistrationIdFromPref()
        if (regId != null) {
            val timeData = TimeData(duration)
            service.sendTimeStatistic(regId, timeData)
            //Log.d(TAG, "stopSession: timeData $timeData")
        }

        //Log.d(TAG, "stopSession: duration $duration")
        //Log.d(TAG, "stopSession: regId $regId")
        Log.d(TAG, "stopSession")
    }

    fun setTags(key: String, value: String) {
        val pushCache = PushCache()
        if (key == null && value == null) {
            pushCache.saveTagKey("")
            pushCache.saveTagValue("")
        } else {
            pushCache.saveTagKey(key)
            pushCache.saveTagValue(value)
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        Log.d(TAG, "onActivityCreated()")
        askNotificationPermission()
    }

    override fun onActivityStarted(p0: Activity) {
        Log.d(TAG, "onActivityStarted()")
    }

    override fun onActivityResumed(p0: Activity) {
        sendTransition(service)
        openUrl()
        Log.d(TAG, "onActivityResumed()")
    }

    override fun onActivityPaused(p0: Activity) {
        Log.d(TAG, "onActivityPaused()")
    }

    override fun onActivityStopped(p0: Activity) {
        stopSession()
        Log.d(TAG, "onActivityStopped()")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState()")
    }

    override fun onActivityDestroyed(p0: Activity) {

        Log.d(TAG, "onActivityDestroyed()")
    }
}