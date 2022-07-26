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

        setOnClickListeners()
        initViews()
        requestRequiredPermission()
    }

    private fun setOnClickListeners() {
        binding.btnMainContacts.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        binding.btnMainMessages.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        binding.btnMainAddcontact.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }

    private fun initViews() {
        binding.btnMainContacts.isEnabled = !isReadContactsNotGranted()
        binding.btnMainMessages.isEnabled = !isReadSMSNotGranted()
    }

    private fun requestRequiredPermission() {
        if(isReadContactsNotGranted() || isReadSMSNotGranted() || isReceiveSMSNotGranted() || isReceiveMMSNotGranted() || isWriteContactsNotGranted()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.WRITE_CONTACTS), 100)
        }
    }

    private fun isReadContactsNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED

    private fun isReadSMSNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED

    private fun isReceiveSMSNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED

    private fun isReceiveMMSNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_MMS) == PackageManager.PERMISSION_DENIED

    private fun isWriteContactsNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED

}