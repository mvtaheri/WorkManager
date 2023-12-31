package com.vahid.workmanager

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {
    @GET("/wp-content/uploads/sites/119/2020/12/tes_gen_blog_code7-1-800x412.jpg")
    suspend fun downloadImage(): Response<ResponseBody>


    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://usa.bootcampcdn.com")
                .build()
                .create(FileApi::class.java)
        }
    }
}