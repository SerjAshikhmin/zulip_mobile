package ru.tinkoff.android.coursework.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import javax.inject.Singleton

@Module
internal class NetModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(AuthInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json, contentType: MediaType): Converter.Factory {
        return json.asConverterFactory(contentType)
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Provides
    @Singleton
    fun provideContentType(): MediaType {
        return "application/json".toMediaType()
    }

    @Provides
    @Singleton
    fun provideZulipJsonApi(retrofit: Retrofit): ZulipJsonApi {
        return retrofit.create(ZulipJsonApi::class.java)
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

    companion object {

        private const val BASE_URL = "https://tinkoff-android-spring-2022.zulipchat.com/"
        private const val AUTH_HEADER_NAME = "Authorization"
        private const val AUTH_TOKEN_VALUE = "Basic c2VyYXNoaWhtaW5AeWFuZGV4LnJ1OnZ2RUJwcFRwRTVvWmg2dVZCRDJ0WEFoY05sdjl1dXlK"
    }

}
