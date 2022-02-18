package com.tenutz.contactsmessagesfetching

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle

import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.SmsMessage
import android.util.Log
import android.text.TextUtils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.text.MessageFormat


class MmsReceiver : BroadcastReceiver() {
    private var _context: Context? = null
    override fun onReceive(`$context`: Context, `$intent`: Intent) {
        _context = `$context`
        val runn = Runnable { parseMMS() }
        val handler = Handler()
        handler.postDelayed(runn, 6000) // 시간이 너무 짧으면 못 가져오는게 있더라
    }

    @SuppressLint("LongLogTag", "Range")
    private fun parseMMS() {
        val contentResolver = _context!!.contentResolver
        val projection = arrayOf("_id")
        val uri: Uri = Uri.parse("content://mms")
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, "_id desc limit 1")
        cursor?.let {
            if (cursor.count == 0) {
                cursor.close()
                return
            }
            cursor.moveToFirst()
            val id: String = cursor.getString(cursor.getColumnIndex("_id"))
            cursor.close()
            val number = parseNumber(id)
            val msg = parseMessage(id)
            Log.i("MMSReceiver.java | parseMMS", "|$number|$msg")
        }
    }

    @SuppressLint("Range")
    private fun parseNumber(`$id`: String): String? {
        var result: String? = null
        val uri: Uri = Uri.parse(MessageFormat.format("content://mms/{0}/addr", `$id`))
        val projection = arrayOf("address")
        val selection = "msg_id = ? and type = 137" // type=137은 발신자
        val selectionArgs = arrayOf(`$id`)
        val cursor: Cursor? = _context!!.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "_id asc limit 1"
        )
        cursor?.let {
            if (cursor.count == 0) {
                cursor.close()
                return result
            }
            cursor.moveToFirst()
            result = cursor.getString(cursor.getColumnIndex("address"))
            cursor.close()
        }
        return result
    }

    @SuppressLint("LongLogTag", "Range")
    private fun parseMessage(`$id`: String): String? {
        var result: String? = null

        // 조회에 조건을 넣게되면 가장 마지막 한두개의 mms를 가져오지 않는다.
        val cursor: Cursor? = _context!!.contentResolver.query(
            Uri.parse("content://mms/part"),
            arrayOf("mid", "_id", "ct", "_data", "text"),
            null,
            null,
            null
        )
        cursor?.let {
            Log.i(
                "MMSReceiver.java | parseMessage",
                "|mms 메시지 갯수 : " + cursor.getCount().toString() + "|"
            )
            if (cursor.count == 0) {
                cursor.close()
                return result
            }
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val mid: String = cursor.getString(cursor.getColumnIndex("mid"))
                if (`$id` == mid) {
                    val partId: String = cursor.getString(cursor.getColumnIndex("_id"))
                    val type: String = cursor.getString(cursor.getColumnIndex("ct"))
                    if ("text/plain" == type) {
                        val data: String? = cursor.getString(cursor.getColumnIndex("_data"))
                        result =
                            if (data.isNullOrBlank()) cursor.getString(cursor.getColumnIndex("text")) else parseMessageWithPartId(
                                partId
                            )
                    }
                }
                cursor.moveToNext()
            }
            cursor.close()
        }
        return result
    }

    private fun parseMessageWithPartId(`$id`: String): String {
        val partURI: Uri = Uri.parse("content://mms/part/$`$id`")
        var `is`: InputStream? = null
        val sb = StringBuilder()
        try {
            `is` = _context!!.contentResolver.openInputStream(partURI)
            if (`is` != null) {
                val isr = InputStreamReader(`is`, "UTF-8")
                val reader = BufferedReader(isr)
                var temp: String = reader.readLine()
                while (!TextUtils.isEmpty(temp)) {
                    sb.append(temp)
                    temp = reader.readLine()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                }
            }
        }
        return sb.toString()
    }
}