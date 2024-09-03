package com.trainingtimer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.trainingtimer.utils.DataService
import com.trainingtimer.views.details.TimerService
import com.trainingtimer.views.details.TrainingFragment
import com.trainingtimer.views.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        applicationContext.startService(Intent(applicationContext, DataService::class.java))

        setupInitialFragment()
    }

    private fun setupInitialFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        if (currentFragment == null) {
            showSplashFragment()
        } else if (TimerService.isCounting) {
            replaceWithTrainingFragment()
        }
    }

    private fun showSplashFragment() {
        supportFragmentManager.commit {
            add(R.id.nav_host_fragment, SplashFragment())
        }
    }

    private fun replaceWithTrainingFragment() {
        supportFragmentManager.commit {
            replace(R.id.nav_host_fragment, TrainingFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}