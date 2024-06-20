package com.trainingtimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.trainingtimer.views.details.TimerService
import com.trainingtimer.views.details.TrainingFragment
import com.trainingtimer.views.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment == null) {
            val fragment = SplashFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.nav_host_fragment, fragment)
                .commit()
        }

        if (TimerService.isCounting) {
            supportFragmentManager.commit {
                replace(R.id.nav_host_fragment, TrainingFragment())
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }
}