package ru.tinkoff.android.coursework.di

import dagger.Component
import retrofit2.Retrofit
import ru.tinkoff.android.coursework.data.LoginRepositoryImpl
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi

@RootScope
@Component(
    modules = [NetModule::class],
    dependencies = [ApplicationComponent::class]
)
internal interface NetworkComponent {

    fun getRetrofit(): Retrofit

    fun getZulipJsonApi(): ZulipJsonApi

    fun getLoginRepository(): LoginRepositoryImpl

}
