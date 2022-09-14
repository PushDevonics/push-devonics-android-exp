package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class PushUser(

    @SerializedName("registration_id")
    private val registrationId: String,

    @SerializedName("uuid")
    private val internalId: String,

    @SerializedName("app_id")
    private val appId: String,

    @SerializedName("platform_id")
    private val platformId: Int = 1,

    private val country: String,
    private val language: String,

    @SerializedName("timezone")
    private val timeZone: String,

    @SerializedName("device_model")
    private val deviceModel: String,
) {
    fun getRegistrationId(): String {
        return registrationId
    }
}
