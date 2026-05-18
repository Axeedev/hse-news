package com.example.news.di

import com.example.news.data.remote.ApiService
import com.example.news.data.repository.NewsRepositoryImpl
import com.example.news.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {


    @Singleton
    @Binds
    fun bindRepository(newsRepositoryImpl: NewsRepositoryImpl): NewsRepository

    companion object {

        @Singleton
        @Provides
        fun provideJson() : Json{
            return Json{
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Singleton
        @Provides
        fun provideClient() = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build()


        @Singleton
        @Provides
        fun provideConverterFactory(json: Json): Converter.Factory{
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Singleton
        @Provides
        fun provideRetrofit(converterFactory: Converter.Factory, client: OkHttpClient): Retrofit{
            return Retrofit
                .Builder()
                .baseUrl("https://newsapi.org/")
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
        }
        @Singleton
        @Provides
        fun provideApiService(retrofit: Retrofit): ApiService{
            return retrofit.create(ApiService::class.java)
        }

    }

}