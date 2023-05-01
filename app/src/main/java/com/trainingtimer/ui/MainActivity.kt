package com.trainingtimer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trainingtimer.R

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity()/*, TrainingListFragment.Callbacks*/ {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = TrainingFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    /*TODO: for future trainingList recycler
    override fun onTrainingSelected(trainingId: UUID) {
        val fragment = TrainingFragment.newInstance(trainingId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }*/
}