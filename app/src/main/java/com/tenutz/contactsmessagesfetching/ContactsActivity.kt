package com.tenutz.contactsmessagesfetching

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
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
        fetchPhoneContacts()
    }

    private fun initAdapter() {
        binding.recyclerContacts.adapter = adapter
    }

    @SuppressLint("Range", "Recycle")
    private fun fetchPhoneContacts() {
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

                //phone types
                //type 1: 010-1234-1234 -> -가 2개인 경우
                //type 2: 01012341234 -> -가 없는 경우
                //type 3: +821012341234 -> +가 맨 앞에 오는 경우
                //type 4: 1234-1234 -> -가 1개인 경우
                contacts.add(Contact(id, name, phone))
            }
        }

        adapter.contacts = contacts
    }
}