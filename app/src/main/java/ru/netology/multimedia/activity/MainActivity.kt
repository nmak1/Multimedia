package ru.netology.multimedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.multimedia.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder, MainFragment.newInstance())
            .commit()
        setContentView(R.layout.activity_main)
    }
}