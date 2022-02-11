package com.tenutz.contactsmessagesfetching

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tenutz.contactsmessagesfetching.databinding.ActivityMessagesBinding

class MessagesActivity: AppCompatActivity() {

    lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}