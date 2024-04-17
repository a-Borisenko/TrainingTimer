package com.trainingtimer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.trainingtimer.utils.DataService
import com.trainingtimer.views.details.TimerService
import com.trainingtimer.views.details.TrainingFragment
import com.trainingtimer.views.list.TrainingListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = TrainingListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }

        if (TimerService.isCounting) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, TrainingFragment())
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        startService(Intent(applicationContext, DataService::class.java))
    }
}