package com.pareekdevansh.cftracker

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.pareekdevansh.cftracker.api.CFApi
import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.Timestamp
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_contest, R.id.navigation_search, R.id.navigation_profile
            )
        )
//        val stamp = System.currentTimeMillis()
//        val date = Date(stamp)
//        Log.d("test" , date.toString())

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        getContestList()
        getUser()
    }

    private fun getUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =  RetrofitInstance.api.searchUser(listOf("devanshpareek"))
                if (response.isSuccessful){
                    Log.d("user" , response.body().toString())
                }
            }
            catch (e  :Exception ) {
                Log.d("user", e.message.toString())
            }
        }
    }

    private fun getContestList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =  RetrofitInstance.api.getContests()
                if (response.isSuccessful){
                    Log.d("contest" , response.body().toString())
                }
                else{
                    Log.d("contest" , "found some error")

                }
            }
            catch (e  :Exception ) {
                Log.d("contest", e.message.toString())
            }
        }

    }

}