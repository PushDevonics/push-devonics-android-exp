package pro.devonics.push

import java.util.*

abstract class UserState {


    companion object {
        private val LOCK = Any()

        val TAGS = "tags"

        const val PUSH_STATUS_SUBSCRIBED = 1
        const val PUSH_STATUS_INVALID_FCM_SENDER_ID = -6
        const val PUSH_STATUS_OUTDATED_GOOGLE_PLAY_SERVICES_APP = -7
        const val PUSH_STATUS_FIREBASE_FCM_INIT_ERROR = -8
        const val PUSH_STATUS_FIREBASE_FCM_ERROR_SERVICE_NOT_AVAILABLE = -9
        const val PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION = -11
        const val PUSH_STATUS_FIREBASE_FCM_ERROR_MISC_EXCEPTION = -12

        private val LOCATION_FIELDS =
            arrayOf("lat", "long", "loc_acc", "loc_type", "loc_bg", "loc_time_stamp")
        private val LOCATION_FIELDS_SET =
            HashSet<String>(Arrays.asList<String>(*LOCATION_FIELDS))
    }
}