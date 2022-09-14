package pro.devonics.push.network

import android.util.Base64
import pro.devonics.push.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private val baseUrl = Base64.decode(BASE_URL, 8).decodeToString()

    private val loggingInterceptor: HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BASIC
        }

    private val okhttpClient = OkHttpClient().newBuilder()
        .addInterceptor(loggingInterceptor)
        .build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(okhttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}