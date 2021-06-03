package com.chugunov.populartvwatcher.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.chugunov.populartvwatcher.R
import com.chugunov.populartvwatcher.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()

        binding.navView.setupWithNavController(findNavController(R.id.nav_host_fragment))

    }

    //переход на другой фрагмент с анимацией и аргументами
    fun changeFragment(destinationId: Int, bundle: Bundle?= null){

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).
            build()

        findNavController(R.id.nav_host_fragment).navigate(destinationId, bundle, navOptions)
    }

    //обработка случая нахождения во фрагменте с избранными
    override fun onBackPressed() {

        if(findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.tvFavoriteFragment)
            finish()
        else
            super.onBackPressed()

    }

}