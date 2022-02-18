package com.tenutz.contactsmessagesfetching

import android.os.Bundle

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.SmsMessage
import android.util.Log


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        Log.d(TAG, "BroadcastReceiver Received")
        if ("android.provider.Telephony.SMS_RECEIVED" == intent.action) {
            val bundle = intent.extras
            val messages = bundle!!["pdus"] as Array<*>?
            val smsMessage: Array<SmsMessage?> = arrayOfNulls(messages!!.size)
            for (i in messages.indices) {
                smsMessage[i] = SmsMessage.createFromPdu(messages[i] as ByteArray)
            }
            val message: String = smsMessage[0]?.messageBody.toString()
            Log.d(TAG, "SMS Message: $message")
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}