package ru.tinkoff.android.coursework.api

import io.reactivex.Single
import retrofit2.http.*
import ru.tinkoff.android.coursework.api.model.User
import ru.tinkoff.android.coursework.api.model.response.AllChannelsListResponse
import ru.tinkoff.android.coursework.api.model.response.AllUsersListResponse
import ru.tinkoff.android.coursework.api.model.response.MessagesListResponse
import ru.tinkoff.android.coursework.api.model.response.ReactionResponse
import ru.tinkoff.android.coursework.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.api.model.response.SubscribedChannelsListResponse
import ru.tinkoff.android.coursework.api.model.response.TopicsListResponse
import ru.tinkoff.android.coursework.api.model.response.UserPresenceResponse

internal interface ZulipJsonApi {

    @GET("api/v1/streams")
    fun getAllStreams(): Single<AllChannelsListResponse>

    @GET("api/v1/users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedChannelsListResponse>

    @GET("api/v1/users/me/{stream_id}/topics")
    fun getTopicsInStream(
        @Path("stream_id") streamId: Long
    ): Single<TopicsListResponse>

    @GET("api/v1/users")
    fun getAllUsers(): Single<AllUsersListResponse>

    @GET("api/v1/users/{user_id_or_email}/presence")
    fun getUserPresence(
        @Path("user_id_or_email") userIdOrEmail: String
    ): Single<UserPresenceResponse>

    @GET("api/v1/users/me")
    fun getOwnUser(): Single<User>

    @GET("api/v1/messages")
    fun getMessages(
        @Query("num_before") numBefore: Int = 100,
        @Query("num_after") numAfter: Int = 100,
        @Query("anchor") anchor: String = "first_unread",
        @Query(value = "narrow", encoded = true) narrow: String
    ): Single<MessagesListResponse>

    @POST("api/v1/messages")
    fun sendMessage(
        @Query("type") type: String = "stream",
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String,
    ): Single<SendMessageResponse>

    @POST("api/v1/messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") messageId: Long,
        @Query("emoji_name") emojiName: String
    ): Single<ReactionResponse>

    @DELETE("api/v1/messages/{message_id}/reactions")
    fun removeReaction(
        @Path("message_id") messageId: Long,
        @Query("emoji_name") emojiName: String
    ): Single<ReactionResponse>

}
