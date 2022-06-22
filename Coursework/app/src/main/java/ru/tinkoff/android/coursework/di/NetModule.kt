package ru.tinkoff.android.coursework.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ru.tinkoff.android.coursework.data.LoginRepositoryImpl
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import java.util.concurrent.TimeUnit

@Module
internal class NetModule(private val baseUrl: String) {

    @Provides
    @RootScope
    fun provideOkHttpClient(loginRepository: LoginRepositoryImpl): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .callTimeout(5, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(AuthInterceptor(loginRepository))
            .build()
    }

    @Provides
    @RootScope
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @RootScope
    fun provideConverterFactory(json: Json, contentType: MediaType): Converter.Factory {
        return json.asConverterFactory(contentType)
    }

    @Provides
    @RootScope
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Provides
    @RootScope
    fun provideContentType(): MediaType {
        return "application/json".toMediaType()
    }

    @Provides
    @RootScope
    fun provideZulipJsonApi(retrofit: Retrofit): ZulipJsonApi {
        return retrofit.create(ZulipJsonApi::class.java)
    }

    @Provides
    @RootScope
    fun provideLoginRepository(): LoginRepositoryImpl {
        return LoginRepositoryImpl()
    }

    inner class AuthInterceptor(private val loginRepository: LoginRepositoryImpl) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val loggedInUser = loginRepository.user
            val authenticatedRequest = if (
                loggedInUser != null
                && loggedInUser.userName.isNotBlank()
                && loggedInUser.apiKey.isNotBlank()
            ) {
                val authToken = Credentials.basic(loggedInUser.userName, loggedInUser.apiKey)
                request.newBuilder()
                    .header(AUTH_HEADER_NAME, authToken)
                    .build()
            } else {
                request.newBuilder()
                    .build()
            }
            return chain.proceed(authenticatedRequest)
        }
    }

    companion object {

        private const val AUTH_HEADER_NAME = "Authorization"
    }

}
