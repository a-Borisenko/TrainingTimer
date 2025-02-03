package com.trainingtimer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.trainingtimer.utils.DataService
import com.trainingtimer.presentation.details.TimerService
import com.trainingtimer.presentation.details.TrainingFragment
import com.trainingtimer.presentation.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationContext.apply {
            Intent(this, DataService::class.java)
        }

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