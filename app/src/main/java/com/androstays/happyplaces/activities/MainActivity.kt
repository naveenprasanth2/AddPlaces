package com.androstays.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androstays.happyplaces.adapters.HappyPlacesAdapter
import com.androstays.happyplaces.database.DatabaseHandler
import com.androstays.happyplaces.databinding.ActivityMainBinding
import com.androstays.happyplaces.model.HappyPlacesModel
import com.androstays.happyplaces.utils.SwipeToEditCallback

class MainActivity: AppCompatActivity() {

    companion object{
        var EXTRA_PLACE_DETAILS = "extra_place_details"
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
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
        val placesAdapter = HappyPlacesAdapter(this, happyPlacesList)
        binding?.rvHappyPlacesList?.adapter = placesAdapter

        placesAdapter.setOnClickListener(object: HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlacesModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })


        // TODO(Step 3: Bind the edit feature class to recyclerview)
        // START
        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // TODO (Step 5: Call the adapter function when it is swiped)
                // START
                val adapter = binding?.rvHappyPlacesList?.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
                // END
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlacesList)
    }

    //shows empty msg if no data is present, else it will show recycler view
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

    override fun onBackPressed() {
        super.onBackPressed()
        this@MainActivity.finish()
    }
    override fun onDestroy(){
        super.onDestroy()
        binding = null
    }
}