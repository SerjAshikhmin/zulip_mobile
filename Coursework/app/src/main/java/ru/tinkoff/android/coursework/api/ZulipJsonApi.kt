package ru.tinkoff.android.coursework.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.tinkoff.android.coursework.api.NetworkService.AUTH_HEADER_NAME
import ru.tinkoff.android.coursework.api.NetworkService.AUTH_TOKEN_VALUE
import ru.tinkoff.android.coursework.model.AllChannelsListResponse
import ru.tinkoff.android.coursework.model.SubscribedChannelsListResponse
import ru.tinkoff.android.coursework.model.TopicsListResponse

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

}
