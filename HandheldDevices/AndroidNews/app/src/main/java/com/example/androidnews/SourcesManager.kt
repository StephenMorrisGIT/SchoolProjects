package com.example.androidnews

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class SourcesManager {

    val okHttpClient: OkHttpClient

    init{
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }
    fun getSources(urlContent: String): List<NewsSources> {
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/sources?language=en&$urlContent&apiKey=ad597c08de3347f9b00107d349dc0dbe")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val newsSources = mutableListOf<NewsSources>()
        val responseString = response.body?.string()

        if (response.isSuccessful && !responseString.isNullOrEmpty()) {
            val json = JSONObject(responseString)
            val sources = json.getJSONArray("sources")

            for (i in 0 until sources.length()) {
                val curr = sources.getJSONObject(i)

                val ID = curr.getString("id")
                val name = curr.getString("name")
                val description = curr.getString("description")

                val source = NewsSources(
                    websiteID = ID,
                    websiteName = name,
                    websiteDescription = description
                )
                newsSources.add(source)
            }
        }
        return newsSources
    }
}