package com.apps.michaeldow.cutransitcompanion.API

import com.apps.michaeldow.cutransitcompanion.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

// REFERENCE: https://android.jlelse.eu/android-networking-in-2019-retrofit-with-kotlins-coroutines-aefe82c4d777
object ApiFactory  {

    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url()
            .newBuilder()
            .addQueryParameter("key", BuildConfig.MTD_API_KEY).build()
        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()
        try {
            chain.proceed(newRequest)
        } catch (e: Exception) {
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_2).code(400).message("FAILED").body(
                ResponseBody.create(MediaType.get("text/plain"), "failed")).build()
        }
    }

    private val mtdClient = OkHttpClient().newBuilder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(mtdClient)
        .baseUrl("https://developer.cumtd.com/api/v2.2/json/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val mtdApi : MtdApi = retrofit().create(MtdApi::class.java)
}