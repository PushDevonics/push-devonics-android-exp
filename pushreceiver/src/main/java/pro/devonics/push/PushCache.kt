package pro.devonics.push

import android.content.Context
import android.util.Log

private const val TAG = "PushCache"

class PushCache {

    private val PUSH_CACHE = "push_cache"
    private val OLD_REGISTRATION_ID = "old_registration_id"
    private val REGISTRATION_ID = "registration_id"
    private val REGISTRATION_STATUS = "registration_status"
    private val SUBSCRIBE_STATUS = "subscribe_status"
    private val INTERNAL_ID = "internal_id"
    private val TAG_KEY = "tag_key"
    private val TAG_VALUE = "tag_value"

    private val appContext = AppContextKeeper.getContext()
    private val ed = appContext
        .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
        ?.edit()

    fun saveTagKey(key: String) {
        ed?.putString(TAG_KEY, key)
        ed?.apply()
    }

    fun getTagKey(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(TAG_KEY, null)
    }

    fun saveTagValue(tagValue: String) {
        ed?.putString(TAG_VALUE, tagValue)
        ed?.apply()
    }

    fun getTagValue(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(TAG_VALUE, null)
    }

    fun saveInternalId(internalId: String) {
        ed?.putString(INTERNAL_ID, internalId)
        ed?.apply()
    }

    fun getInternalIdFromPref(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(INTERNAL_ID, null)
    }

    fun saveOldRegistrationIdPref(old_reg_id: String) {
        ed?.putString(OLD_REGISTRATION_ID, old_reg_id)
        //Log.d(TAG, "aveOldRegistrationIdPref: old_reg_id = $old_reg_id")
        ed?.apply()
    }

    fun getOldRegistrationIdFromPref(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(OLD_REGISTRATION_ID, null)
    }

    fun saveRegistrationIdPref(reg_id: String) {
        ed?.putString(REGISTRATION_ID, reg_id)
        //Log.d(TAG, "saveRegistrationIdPref: reg_id = $reg_id")
        ed?.apply()
    }

    fun getRegistrationIdFromPref(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(REGISTRATION_ID, null)
    }

    fun saveRegistrationStatus(status: Int) {
        ed?.putInt(REGISTRATION_STATUS, status)
        //Log.d(TAG, "saveRegistrationStatus: status = $status")
        ed?.apply()
    }

    fun getRegistrationStatusFromPref(): Int? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getInt(REGISTRATION_STATUS, 0)
    }

    fun saveSubscribeStatus(status: Boolean) {
        ed?.putBoolean(SUBSCRIBE_STATUS, status)
        ed?.apply()
    }
    fun getSubscribeStatusFromPref(): Boolean? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getBoolean(SUBSCRIBE_STATUS, false)
    }
}