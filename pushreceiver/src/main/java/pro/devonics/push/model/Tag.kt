package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("key")
    private val key: String,
    @SerializedName("value")
    private val value: String,

)
