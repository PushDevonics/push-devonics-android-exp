package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class Sender(

    @SerializedName("data")
    val data: Data

) {
    @JvmName("getData1")
    fun getData(): Data {
        return data
    }
}
