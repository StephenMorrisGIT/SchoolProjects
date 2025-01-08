package com.example.androidtweets

import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import edu.gwu.androidtweets.Tweet
import okhttp3.Address
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.net.URLEncoder
import java.util.*


class TwitterManager {

    val okHttpClient: OkHttpClient

    init{
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    private fun encodeSecrets(
        apiKey: String,
        apiSecret: String
    ): String{
        // Encoding for a URL -- converts things like spaces into %20
        val encodedKey = URLEncoder.encode(apiKey, "UTF-8")
        val encodedSecret = URLEncoder.encode(apiSecret, "UTF-8")

        // Concatenate the two together, with a colon inbetween
        val combinedEncoded = "$encodedKey:$encodedSecret"

        // Base-64 encode the combined string
        val base64Combined = android.util.Base64.encodeToString(combinedEncoded.toByteArray(), android.util.Base64.NO_WRAP)

        return base64Combined
    }

    fun retrieveOAuthToken(
        apiKey: String,
        apiSecret: String
    ): String {
        val base64Combined = encodeSecrets(apiKey, apiSecret)
        // return base64Combined

        val requestBody = "grant_type=client_credentials".toRequestBody(
                contentType = "application/x-www-form-urlencoded".toMediaType()
            )

        val request = Request.Builder()
            .url("https://api.twitter.com/oauth2/token")
            .header("Authorization", "Basic $base64Combined")
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseString: String? = response.body?.string()

        if (response.isSuccessful && !responseString.isNullOrEmpty()) {
            val json = JSONObject(responseString)
            return json.getString("access_token")
        } else {
            return ""
        }
    }


    fun retrieveTweets(latitude: Double, longitude: Double, oAuthToken: String): List<Tweet>{
        val searchTerm = "Android"
        val radius = "30mi"

        val request: Request = Request.Builder()
            .url("https://api.twitter.com/1.1/search/tweets.json?q=$searchTerm&geocode=$latitude,$longitude,$radius")
            .header("Authorization", "Bearer $oAuthToken")
            .get()
            .build()

        // Actually makes the API call
        val response: Response = okHttpClient.newCall(request).execute()

        // Empty list of Tweets that we'll build up from the response
        val tweets = mutableListOf<Tweet>()

        // Get the JSON string body, if there was one
        val responseString = response.body?.string()

        // Make sure the server responded successfully, and with some JSON data
        if (response.isSuccessful && !responseString.isNullOrEmpty()) {
            val json = JSONObject(responseString)
            val statuses = json.getJSONArray("statuses")

            for (i in 0 until statuses.length()) {
                // Grab current Tweet
                val curr = statuses.getJSONObject(i)
                // Get the text content Tweet
                val text = curr.getString("text")
                // Get the user object
                val user = curr.getJSONObject("user")
                val name = user.getString("name")
                val handle = user.getString("screen_name")
                val profilePictureUrl = user.getString("profile_image_url_https")

                val tweet = Tweet(
                    username = name,
                    handle = handle,
                    iconUrl = profilePictureUrl,
                    content = text
                )

                tweets.add(tweet)
            }
        }
        else{
        }

        return tweets

    }

}
