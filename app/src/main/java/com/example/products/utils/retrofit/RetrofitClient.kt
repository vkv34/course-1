package com.example.products.utils.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val httpLoggingInterceptor = HttpLoggingInterceptor()
    private val logger =
        httpLoggingInterceptor.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val retrofit: RetrofitInterface by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_API_URL)
            .addConverterFactory(DateConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitInterface::class.java)
    }
}