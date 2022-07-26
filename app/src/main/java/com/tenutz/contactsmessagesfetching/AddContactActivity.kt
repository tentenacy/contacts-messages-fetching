package com.tenutz.contactsmessagesfetching

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tenutz.contactsmessagesfetching.databinding.ActivityAddcontactBinding
import com.tenutz.contactsmessagesfetching.databinding.ActivityContactsBinding
import android.provider.ContactsContract

import android.content.Intent
import java.lang.Exception
import android.content.OperationApplicationException

import android.content.ContentProviderOperation

import android.R
import android.os.RemoteException


class AddContactActivity: AppCompatActivity() {

    lateinit var binding: ActivityAddcontactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddcontactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddcontact.setOnClickListener {
            for(i in 1..100) {
                addContact(i)
            }
//            addContact()
        }
    }

    private fun addContact(index: Int = 1) {
        object : Thread() {
            override fun run() {
                val list: ArrayList<ContentProviderOperation> = ArrayList()
                try {
                    list.add(
                        ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
                    )
                    list.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                            ).withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                "${binding.editAddcontactName.text}${index}"
                            ).build()
                    )
                    list.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                            ).withValue(
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                binding.editAddcontactPhone.text.toString()
                            ).withValue(
                                ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                            ).build()
                    )
                    list.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                            ).withValue(
                                ContactsContract.CommonDataKinds.Email.DATA,
                                binding.editAddcontactEmail.text.toString()
                            ).withValue(
                                ContactsContract.CommonDataKinds.Email.TYPE,
                                ContactsContract.CommonDataKinds.Email.TYPE_WORK
                            ).build()
                    )
                    applicationContext.contentResolver.applyBatch(ContactsContract.AUTHORITY, list)
                    list.clear()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                } catch (e: OperationApplicationException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}