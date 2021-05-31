package com.chugunov.populartvwatcher.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chugunov.populartvwatcher.application.Constant
import com.chugunov.populartvwatcher.databinding.ActivitySplashscreenBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //чтобы после изменения конфигурации не перезапускалось

        if(savedInstanceState?.getBoolean("isInited") == null){

            val waited = Single.create<Boolean> {
                Thread.sleep(Constant.TIME_SPLASHSCREEN)

                it.onSuccess(true)
            }

            waited
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        startMainActivity()
                    }, {
                        startMainActivity()
                    })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isInited", true)
        super.onSaveInstanceState(outState)
    }

    private fun startMainActivity()
    {
        val main = Intent(this, MainActivity::class.java)
        this.startActivity(main)
        this.finish()
    }
}