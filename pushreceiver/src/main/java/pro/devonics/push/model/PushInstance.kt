package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class PushInstance(

    @SerializedName("old_registration_id")
    private val oldRegistrationId: String,

    //@Expose
    //@SerializedName("app_id")
    //private val appId: String,// = "38189031-04b1-4685-8e27-01f184ec0a9e",

    //instanceId is a registrationId
    //@Expose
    @SerializedName("registration_id")
    private val registrationId: String
) {
    fun getOldRegistrationId(): String {
        return oldRegistrationId
    }
    fun getRegistrationId(): String {
        return registrationId
    }
}
