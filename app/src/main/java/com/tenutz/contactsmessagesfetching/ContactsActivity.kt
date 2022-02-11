package com.tenutz.contactsmessagesfetching

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tenutz.contactsmessagesfetching.adapter.ContactsAdapter
import com.tenutz.contactsmessagesfetching.databinding.ActivityContactsBinding
import com.tenutz.contactsmessagesfetching.dto.Contact

class ContactsActivity: AppCompatActivity() {

    lateinit var binding: ActivityContactsBinding

    private val adapter: ContactsAdapter by lazy {
        ContactsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        readPhoneContacts()
    }

    private fun initAdapter() {
        binding.recyclerContacts.adapter = adapter
    }

    @SuppressLint("Range", "Recycle")
    private fun readPhoneContacts() {
        val contacts = arrayListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        cursor?.let {
            while(it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(projection[0]))
                val name = it.getString(it.getColumnIndex(projection[1]))
                val phone = it.getString(it.getColumnIndex(projection[2]))

                contacts.add(Contact(id, name, phone))
            }
        }

        adapter.contacts = contacts
    }
}