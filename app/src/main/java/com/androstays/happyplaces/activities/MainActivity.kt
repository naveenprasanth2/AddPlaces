package com.androstays.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androstays.happyplaces.database.DatabaseHandler
import com.androstays.happyplaces.databinding.ActivityMainBinding
import com.androstays.happyplaces.model.HappyPlacesModel

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            intent = Intent(this@MainActivity, AddHappyPlacesActivity::class.java)
            startActivity(intent)
        }
        getHappyPLacesListFromLocalDB()
    }

    private fun getHappyPLacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList: ArrayList<HappyPlacesModel> = dbHandler.getHappyPlacesList()
        if(getHappyPlacesList.isNotEmpty()){
            getHappyPlacesList.map { x -> x.title }.forEach{ x -> println("this is the title $x")}
        }

    }

    override fun onDestroy(){
        super.onDestroy()
        binding = null
    }
}