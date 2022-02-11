package com.tenutz.contactsmessagesfetching

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tenutz.contactsmessagesfetching.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainContacts.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        binding.btnMainMessages.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        initViews()
        requestRequiredPermission()
    }

    private fun initViews() {
        binding.btnMainContacts.isEnabled = !isReadContactsNotGranted()
    }

    private fun requestRequiredPermission() {
        if(isReadContactsNotGranted()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        }
    }

    private fun isReadContactsNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
}