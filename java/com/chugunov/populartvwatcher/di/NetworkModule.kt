package com.chugunov.populartvwatcher.di

import com.chugunov.populartvwatcher.retrofit.api.TvApi
import com.chugunov.populartvwatcher.application.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(Constant.BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideTvApi(retrofit: Retrofit): TvApi = retrofit.create(TvApi::class.java)



    @Provides
    @Singleton
    fun provideLoginInterceptor() : HttpLoggingInterceptor {
        var interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }



    @Provides
    @Singleton
    fun provideHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor) = OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()
}