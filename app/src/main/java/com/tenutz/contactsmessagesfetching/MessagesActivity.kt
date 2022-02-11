package com.tenutz.contactsmessagesfetching

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tenutz.contactsmessagesfetching.adapter.MessagesAdapter
import com.tenutz.contactsmessagesfetching.databinding.ActivityMessagesBinding
import com.tenutz.contactsmessagesfetching.dto.Message

class MessagesActivity : AppCompatActivity() {

    lateinit var binding: ActivityMessagesBinding

    val adapter: MessagesAdapter by lazy {
        MessagesAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        fetchSMSMessages()
    }

    private fun initAdapter() {
        binding.recyclerMessages.adapter = adapter
    }

    private fun fetchSMSMessages() {

        val messages = arrayListOf<Message>()

        val cursor = contentResolver.query(
            Uri.parse("content://mms-sms/conversations"),
            arrayOf("_id", "thread_id", "address", "person", "date", "body"),
            null,
            null,
            "date DESC"
        )
        cursor?.let {
            while (it.moveToNext()) {
                val messageId = it.getLong(0)
                val threadId = it.getLong(1)
                val address = it.getString(2)
                val contactId = it.getLong(3)
                val timeStamp = it.getLong(4)
                val body = it.getString(5)

                messages.add(
                    Message(
                        messageId = messageId.toString(),
                        threadId = threadId.toString(),
                        address = address,
                        contactId = contactId.toString(),
                        timeStamp = timeStamp,
                        body = body,
                        contactIdString = contactId.toString()
                    )
                )
            }

            it.close()
        }

        adapter.messages = messages
    }
}