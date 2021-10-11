package com.trainingtimer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var addButton: Button

    private val trainingBank = listOf(
        Training(title = R.string.training_pulls.toString(), sets = 6, times = 8),
        Training(title = R.string.training_pushes.toString(), sets = 10, times = 20)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButton = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            //выполнить после нажатия
        }
    }
}