package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class Internal(
    @SerializedName("internal_id")
    private val internalId: String
) {
    fun getInternalId(): String {
        return internalId
    }
}
