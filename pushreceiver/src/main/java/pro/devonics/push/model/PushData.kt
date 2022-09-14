package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class PushData(
    @SerializedName("sent_push_id")
    val sentPushId: String
)
