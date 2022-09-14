package pro.devonics.push

import android.content.Context

class HelperCache(context: Context) {
    private val mContext = context
    private val HELPER_CACHE = "helper_cache"
    private val TRANSITION_ST = "transition"
    private val SENT_PUSH_ID = "sent_push_id"
    private val OPEN_URL = "open-url"
    private val DEEPLINK = "deeplink"

    private val ed = mContext
        .getSharedPreferences(HELPER_CACHE, Context.MODE_PRIVATE)
        ?.edit()

    fun saveSentPushId(sentPushId: String?) {
        ed?.putString(SENT_PUSH_ID, sentPushId)
        ed?.apply()
    }

    fun getSentPushId(): String? {
        return mContext
            .getSharedPreferences(HELPER_CACHE, Context.MODE_PRIVATE)
            .getString(SENT_PUSH_ID, null)
    }

    fun saveOpenUrl(openUrl: String?) {
        ed?.putString(OPEN_URL, openUrl)
        ed?.apply()
    }

    fun getOpenUrl(): String? {
        return mContext
            .getSharedPreferences(HELPER_CACHE, Context.MODE_PRIVATE)
            .getString(OPEN_URL, null)
    }

    fun saveDeeplink(deeplink: String) {
        ed?.putString(DEEPLINK, deeplink)
        ed?.apply()
    }

    fun getDeeplink(): String? {
        return mContext
            .getSharedPreferences(HELPER_CACHE, Context.MODE_PRIVATE)
            .getString(DEEPLINK, null)
    }

    fun saveTransition(transition: Boolean) {
        ed?.putBoolean(HELPER_CACHE, transition)
        ed?.apply()
    }

    fun getTransitionSt(): Boolean? {
        return mContext
            .getSharedPreferences(HELPER_CACHE, Context.MODE_PRIVATE)
            ?.getBoolean(TRANSITION_ST, false)
    }
}