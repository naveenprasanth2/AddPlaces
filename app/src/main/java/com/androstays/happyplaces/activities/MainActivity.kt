package com.androstays.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androstays.happyplaces.adapters.HappyPlacesAdapter
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

    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlacesModel>){
        binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
        binding?.rvHappyPlacesList?.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter( happyPlacesList)
        binding?.rvHappyPlacesList?.adapter = placesAdapter

    }

    private fun getHappyPLacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList: ArrayList<HappyPlacesModel> = dbHandler.getHappyPlacesList()
        if(getHappyPlacesList.isNotEmpty()){
            binding?.rvHappyPlacesList?.visibility = View.VISIBLE
            binding?.tvNoRecordsFound?.visibility = View.INVISIBLE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        }else{
            binding?.rvHappyPlacesList?.visibility = View.INVISIBLE
            binding?.tvNoRecordsFound?.visibility = View.VISIBLE
        }

    }

    override fun onDestroy(){
        super.onDestroy()
        binding = null
    }
}