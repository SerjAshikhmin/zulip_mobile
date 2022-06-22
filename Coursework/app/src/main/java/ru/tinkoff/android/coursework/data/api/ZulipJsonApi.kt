package ru.tinkoff.android.coursework.data.api

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.api.model.response.*

internal interface ZulipJsonApi {

    @POST("api/v1/fetch_api_key")
    suspend fun fetchApiKey(
        @Query("username") userName: String,
        @Query("password") password: String
    ): FetchApiKeyResponse

    /*@POST("api/v1/fetch_api_key")
    fun fetchApiKey(
        @Query("username") userName: String,
        @Query("password") password: String
    ): Single<FetchApiKeyResponse>*/

    @GET("api/v1/streams")
    fun getAllStreams(): Single<AllStreamsListResponse>

    @GET("api/v1/users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamsListResponse>

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
    fun getOwnUser(): Single<UserDto>

    @GET("api/v1/messages")
    fun getMessages(
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int = NUMBER_OF_MESSAGES_AFTER_ANCHOR,
        @Query("anchor") anchor: String = "first_unread",
        @Query(value = "narrow", encoded = true) narrow: String
    ): Single<MessagesListResponse>

    @POST("api/v1/messages")
    fun sendMessage(
        @Query("type") type: String = "stream",
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String
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

    @Multipart
    @POST("api/v1/user_uploads")
    fun uploadFile(
        @Part file: MultipartBody.Part
    ): Single<UploadFileResponse>

    @GET("api/v1/messages/{msg_id}")
    fun loadSingleMessage(
        @Path("msg_id") messageId: Long
    ): Single<LoadSingleMessageResponse>

    @DELETE("api/v1/messages/{msg_id}")
    fun deleteMessage(
        @Path("msg_id") messageId: Long
    ): Single<ActionWithMessageResponse>

    @PATCH("api/v1/messages/{msg_id}")
    fun editMessage(
        @Path("msg_id") messageId: Long,
        @Query("topic") topic: String,
        @Query("content") content: String
    ): Single<ActionWithMessageResponse>

    @POST("api/v1/users/me/subscriptions")
    fun subscribeToStream(
        @Query("invite_only") inviteOnly: Boolean = false,
        @Query(value = "subscriptions", encoded = true) subscriptions: String
    ): Single<SubscribeToStreamResponse>

    companion object {

        internal const val NUMBER_OF_MESSAGES_AFTER_ANCHOR = 0
        internal const val LAST_MESSAGE_ANCHOR = 10000000000000000L
        internal const val TOPIC_NARROW_OPERATOR_KEY = "topic"
        internal const val STREAM_NARROW_OPERATOR_KEY = "stream"
    }

}
