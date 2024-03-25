package com.trainingtimer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.trainingtimer.views.details.TimerService
import com.trainingtimer.views.details.TrainingFragment
import com.trainingtimer.views.list.TrainingListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (TimerService.isCounting) {
            Log.d("main", "counting")
            fragment = TrainingFragment()
        } else {
            Log.d("main", "stopped")
            fragment = TrainingListFragment()
        }

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
//            val fragment = TrainingListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    companion object {
        private var fragment: Fragment = TrainingListFragment()
    }
}