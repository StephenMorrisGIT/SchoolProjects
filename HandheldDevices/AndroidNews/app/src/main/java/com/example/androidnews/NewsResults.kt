package com.example.androidnews

import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import org.jetbrains.anko.doAsync
import java.lang.Exception

class NewsResults : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_results)

        val intent = getIntent()

        // Receiving the address we chose on map from MapsActivity
        // Address in quotes is the key passed in from MapsActivity
        // val address: Address = intent.getParcelableExtra<Address>("address")!!
        // val city = address.locality ?: "Unknown"
        // val searchString = city.toString()

        val search = intent.getStringExtra("quickSearch")

        // Set the page header to reflect location
        // val localizedString = getString(R.string.news_results_title, city)
        // setTitle(localizedString)
        val searchString = search.toString()
        setTitle("News About $searchString")


        recyclerView = findViewById(R.id.recyclerView)


        doAsync {
            val newsManager = NewsManager()
            try{
                val news = newsManager.getNews(
                    // urlContent = "everything?q=Boston"
                    urlContent = "everything?q=$searchString&language=en"
                )
                val adapter = ArticlesAdapter(news)

                runOnUiThread{
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@NewsResults)
                }


            } catch (exception: Exception){
                Log.e("NewResults", "Retrieving News failed", exception)

            }
        }
    }

}