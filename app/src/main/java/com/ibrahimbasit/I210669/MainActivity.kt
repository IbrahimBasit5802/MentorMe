package com.ibrahimbasit.I210669

import UserViewModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ibrahimbasit.I210669.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var userMail = intent.getStringExtra("email")
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        userViewModel.getUserProfile(userMail.toString())

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