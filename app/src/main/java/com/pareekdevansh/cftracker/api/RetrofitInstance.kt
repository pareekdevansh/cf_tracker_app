package com.pareekdevansh.cftracker.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL ="https://codeforces.com/api/"
class RetrofitInstance {
//    companion object{
//        private val retrofit by lazy{
//
//            val logging = HttpLoggingInterceptor()
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//
//            val client = OkHttpClient.Builder()
//                .addInterceptor(logging)
//                .build()
//
//            Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build()
//        }
//
//        val api by lazy {
//            retrofit.create(CFApi:: class.java)
//        }
//    }
 companion object{
    private var retrofitObject: Retrofit? = null
        fun api(): CFApi? {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
//            val client: OkHttpClient = Retrofit.Builder().addInterceptor(interceptor).build()
            if (retrofitObject == null) {
                retrofitObject = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitObject?.create(CFApi::class.java)
        }
 }
}