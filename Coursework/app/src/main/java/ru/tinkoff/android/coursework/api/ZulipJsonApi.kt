package ru.tinkoff.android.coursework.api

import io.reactivex.Single
import retrofit2.http.*
import ru.tinkoff.android.coursework.api.NetworkService.AUTH_HEADER_NAME
import ru.tinkoff.android.coursework.api.NetworkService.AUTH_TOKEN_VALUE
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.model.response.AllChannelsListResponse
import ru.tinkoff.android.coursework.model.response.AllUsersListResponse
import ru.tinkoff.android.coursework.model.response.MessagesListResponse
import ru.tinkoff.android.coursework.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.model.response.SubscribedChannelsListResponse
import ru.tinkoff.android.coursework.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.model.response.UserPresenceResponse

internal interface ZulipJsonApi {

    @GET("api/v1/streams")
    fun getAllStreams(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE
    ): Single<AllChannelsListResponse>

    @GET("api/v1/users/me/subscriptions")
    fun getSubscribedStreams(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE
    ): Single<SubscribedChannelsListResponse>

    @GET("api/v1/users/me/{stream_id}/topics")
    fun getTopicsInStream(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE,
        @Path("stream_id") streamId: Long
    ): Single<TopicsListResponse>

    @GET("api/v1/users")
    fun getAllUsers(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE
    ): Single<AllUsersListResponse>

    @GET("api/v1/users/{user_id_or_email}/presence")
    fun getUserPresence(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE,
        @Path("user_id_or_email") userIdOrEmail: String
    ): Single<UserPresenceResponse>

    @GET("api/v1/users/me")
    fun getOwnUser(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE
    ): Single<User>

    @GET("api/v1/messages")
    fun getMessages(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE,
        @Query("num_before") numBefore: Int = 100,
        @Query("num_after") numAfter: Int = 100,
        @Query("anchor") anchor: String = "first_unread",
        @Query(value = "narrow", encoded = true) narrow: String
    ): Single<MessagesListResponse>

    @POST("api/v1/messages")
    fun sendMessage(
        @Header(AUTH_HEADER_NAME) authorization: String = AUTH_TOKEN_VALUE,
        @Query("type") type: String = "stream",
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String,
    ): Single<SendMessageResponse>

}
