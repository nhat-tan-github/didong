package com.example.myapplication.activity

import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.AcctivityAccountBinding

class acctivity_account : AppCompatActivity() {
    private lateinit var binding: AcctivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcctivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener{
            finish()
        }
        binding.logout.setOnClickListener{
            clearSession()
            finishAffinity()
            System.exit(0)
        }

    }
    private fun clearSession() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}