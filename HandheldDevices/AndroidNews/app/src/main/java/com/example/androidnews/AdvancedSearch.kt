package com.example.androidnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import java.lang.Exception

class AdvancedSearch : AppCompatActivity() {
    // private lateinit var categorySpinner2: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var seeResults: Button
    // private lateinit var checkBox: CheckBox
    private lateinit var sourcesSelectedText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search2)

        val intent = getIntent()
        val search = intent.getStringExtra("advancedSearch")

        // val localizedString = getString("Advanced Search for $search")
        val localizedString = "Advanced Search for $search"
        setTitle(localizedString)

        var sourcesSelected: Int = 0
        var sourcesString: String = ""

        recyclerView = findViewById(R.id.recyclerView2)
        // categorySpinner2 = findViewById(R.id.categorySpinner2)
        seeResults = findViewById(R.id.resultsButton)
        // checkBox = findViewById(R.id.sourceCheckBox)
        sourcesSelectedText = findViewById(R.id.sourcesSelectedText)
        seeResults.isEnabled = false

        val spinner: Spinner = findViewById(R.id.categorySpinner2)
        ArrayAdapter.createFromResource(this, R.array.spinnerCategories, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sourcesSelected = 0
                sourcesString = ""
                val spinnerSelection = parent?.getItemAtPosition(position)
                val category = spinnerSelection.toString()

                doAsync {
                    val sourcesManager = SourcesManager()
                    try{
                        val source = sourcesManager.getSources(
                            urlContent = "category=$category"
                        )

                        val adapter = SourcesAdapter(source)
                        runOnUiThread{
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this@AdvancedSearch)
                        }
                    } catch (exception: Exception){}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        /*
        checkBox.setOnCheckedChangeListener { checkView, isChecked ->
            if(isChecked){
                var source: String = ""

                Log.d("AdvancedSearch", "checkBoxChecked")
                // seeResults.isEnabled = true
                // sourcesSelected++
                // sourcesSelectedText.text = "$sourcesSelected Selected"
                // sourcesString += ","
            }
            else{
                Log.d("AdvancedSearch", "checkBoxCheck Failed")
            }
        }

        fun setMyCustomListener(customListener: SourcesAdapter.MyCustomListener) {
                    source = customListener.somethingHappened(checkView).toString()
                    Log.d("AdvancedSearch", "source = $source")
                }


        fun setMyCustomListener(customListener: SourcesAdapter.MyCustomListener) {
            customListener.somethingHappened("Hello World!")
        }
        */

        seeResults.setOnClickListener{view ->
            Log.d("AdvancedSearch", "seeResultsClick")

            val intent = Intent(this, NewsResults::class.java)
            // intent.putExtra("quickSearch", "$search&sources=$sourcesString")
            startActivity(intent)
        }

    }
}