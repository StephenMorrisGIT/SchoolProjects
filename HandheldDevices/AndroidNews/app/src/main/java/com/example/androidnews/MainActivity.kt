package com.example.androidnews

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.lang.Exception

class MainActivity : AppCompatActivity() /*, AdapterView.OnItemSelectedListener*/{
    // will initialize these vars later
    private lateinit var searchBar: EditText
    private lateinit var quickSearch: Button
    private lateinit var advancedSearch: Button
    private lateinit var viewMap: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Create Instance
        val sharedPrefs: SharedPreferences = getSharedPreferences(
            "Android News",
            Context.MODE_PRIVATE
        ) // Get the saved information from the previous session

        setContentView(R.layout.activity_main) // Initialize layout activity_main

        // Initialize all of the interactive elements of the main activity
        searchBar = findViewById(R.id.userSearch)
        quickSearch = findViewById(R.id.quickSearchButton)
        advancedSearch = findViewById(R.id.advancedSearchButton)
        viewMap = findViewById(R.id.viewMapButton)
        categorySpinner = findViewById(R.id.spinner)
        recyclerView = findViewById(R.id.recyclerView)

        // Disable both search button unless there is something in the search bar
        quickSearch.isEnabled = false
        advancedSearch.isEnabled = false

        viewMap.isEnabled = true
        var inputtedSearch: String = ""


        // Enabling quickSearch and advancedSearch if there is text in the search bar
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val enableButton = searchBar.text.isNotEmpty()

                // Saves the text put into the search bar
                // sharedPrefs.edit().putString("SAVED_SEARCH", searchBar.text.toString()).apply()

                quickSearch.isEnabled = enableButton
                advancedSearch.isEnabled = enableButton

            }
        }

        // the text of searchBar now actually contains the string representation of what was typed
        searchBar.addTextChangedListener(textWatcher)

        val savedSearch: String? = sharedPrefs.getString("SAVED_SEARCH", "")
        searchBar.setText(savedSearch)

        quickSearch.setOnClickListener{view ->
            Log.d("MainActivity", "onClickQuickSearch")
            inputtedSearch = searchBar.text.toString()
            sharedPrefs.edit().putString("SAVED_SEARCH", inputtedSearch).apply()

            val intent = Intent(this, NewsResults::class.java)
            intent.putExtra("quickSearch", savedSearch)
            startActivity(intent)
        }

        advancedSearch.setOnClickListener{view ->
            Log.d("MainActivity", "onClickAdvancedSearch")
            inputtedSearch = searchBar.text.toString()
            sharedPrefs.edit().putString("SAVED_SEARCH", inputtedSearch).apply()

            val intent = Intent(this, AdvancedSearch::class.java)
            intent.putExtra("advancedSearch", savedSearch)
            startActivity(intent)
        }

        viewMap.setOnClickListener { view ->
            Log.d("MainActivity", "onClickViewMap")

            val intent = Intent(this, MapsActivity::class.java)

            startActivity(intent)
        }

        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(this, R.array.spinnerCategories, android.R.layout.simple_spinner_item).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerSelection = parent?.getItemAtPosition(position)
                val category = spinnerSelection.toString()
                sharedPrefs.edit().putString("USER_CATEGORY", category).apply()
                sharedPrefs.edit().putInt("CATEGORY_INDEX", position).apply()

                doAsync {
                    val newsManager = NewsManager()
                    try{
                        val news = newsManager.getNews(
                            urlContent = "top-headlines?country=us&category=$category"
                            // urlContent = "top-headlines?country=us&category=business"
                        )
                        val adapter = ArticlesAdapter(news)

                        runOnUiThread{
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        }
                    } catch (exception: Exception){}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        val savedCategory = sharedPrefs.getString("USER_CATEGORY", "")
        spinner.setSelection(sharedPrefs.getInt("CATEGORY_INDEX", 0))
    }
}