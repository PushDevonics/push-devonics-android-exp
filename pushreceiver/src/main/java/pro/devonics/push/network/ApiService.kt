package pro.devonics.push.network

import pro.devonics.push.*
import pro.devonics.push.model.*
import pro.devonics.push.model.Tag
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //1 Получаем sender_id, который используем для инициализации fcm
    @GET(VIEW)
    fun getSenderId(@Path("uuid") uuid: String): Call<Sender>

    //2 Отсылаем запрос только при первом открытии приложения и сохраняем
    //   registration_id в кеш, отсылаем internal_id
    @POST(SUBSCRIBE_USER)
    fun createPush(@Body pushUser: PushUser): Call<Status>

    //3 Отсылаем каждый раз при открытии приложения
    @GET(SESSION)
    fun createSession(@Path("registrationId") registrationId: String): Call<Status>

    //4 Отсылаем при обновление fcm токена
    @PATCH(UPDATE_USER)
    fun updateUser(@Path("registrationId") registrationId: String): Call<Status>

    //5 Отсылаем свои теги
    @PATCH(SAVE_TAG)
    fun saveCustomParams(@Path("registrationId") registrationId: String,
                         @Body tag: Tag): Call<Status>

    //6 Отсылаем продолжительность работы
    @PATCH(DURATION)
    fun sendDuration(@Path("registrationId") registrationId: String,
                     @Body timeData: TimeData): Call<Status>

    //7 Отсылаем переход по пушу
    @PATCH(TRANSITION)
    fun createTransition(
        @Path("registrationId") registrationId: String,
        @Body  pushData: PushData): Call<Status>
}