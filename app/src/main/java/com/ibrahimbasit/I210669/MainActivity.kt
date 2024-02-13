package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ibrahimbasit.I210669.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.search -> {
                    replaceFragment(SearchFragment())
                }
                R.id.chat -> {
                    replaceFragment(ChatFragment())
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                }


            }
            true
        }

        if (intent.getStringExtra("navigateTo") == "ChatPersonFragment") {
            replaceFragment(ChatPersonFragment())
        }

        val fabButton : FloatingActionButton = findViewById(R.id.fab_add)
        fabButton.setOnClickListener {
            replaceFragment(AddMentorFragment())
        }


    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}