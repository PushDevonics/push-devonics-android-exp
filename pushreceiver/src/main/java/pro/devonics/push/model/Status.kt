package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class Status(
    val status: String,
    @SerializedName("data")
    val internal: Internal
)