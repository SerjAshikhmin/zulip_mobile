package ru.tinkoff.android.coursework.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

internal object NetworkService {

    internal const val AUTH_HEADER_NAME = "Authorization"
    internal const val AUTH_TOKEN_VALUE = "Basic c2VyYXNoaWhtaW5AeWFuZGV4LnJ1OnZ2RUJwcFRwRTVvWmg2dVZCRDJ0WEFoY05sdjl1dXlK"
    private const val BASE_URL = "https://tinkoff-android-spring-2022.zulipchat.com/"

    private val okClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .addInterceptor(AuthInterceptor())
        .build()

    private val contentType = "application/json".toMediaType()

    private var mRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okClient)
        .addConverterFactory(Json{ ignoreUnknownKeys = true }.asConverterFactory(contentType))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    fun getZulipJsonApi(): ZulipJsonApi {
        return mRetrofit.create(ZulipJsonApi::class.java)
    }

    class AuthInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val authenticatedRequest = request.newBuilder()
                .header(AUTH_HEADER_NAME, AUTH_TOKEN_VALUE)
                .build()

            return chain.proceed(authenticatedRequest)
        }
    }

}
