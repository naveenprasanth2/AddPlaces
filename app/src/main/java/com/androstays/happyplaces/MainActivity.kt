package com.androstays.happyplaces

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androstays.happyplaces.databinding.ActivityMainBinding

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
    }


    override fun onDestroy(){
        super.onDestroy()
        binding = null
    }
}