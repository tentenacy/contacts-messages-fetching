package com.tenutz.contactsmessagesfetching

import android.annotation.SuppressLint
import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import android.text.TextUtils

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

//message example
/*[Web발신]
야놀자 당일예약
<대실>
서울 무인호텔
2105021850012345

준특실(주차문의)
30,000원

홍길동 / 050712345678
2021-05-02(일) 19:00~
2021-05-02(일) 23:00 (4시간)
도보방문*/
            val regex = """야놀자 (?:당일|미리)예약[\r\n]+<(대실|숙박|연박)>[\r\n]+([^\r\n]+)[\r\n]+([0-9]+)[\r\n]+([^\(]+)\([^\)]+\)[\r\n]+([0-9,]+)원[\r\n]+([^ ]+) / ([0-9]+)[\r\n]+([0-9]{4}-[0-9]{2}-[0-9]{2})\([월화수목금토일]\) ([0-9]{2}:[0-9]{2})~[\r\n]+([0-9]{4}-[0-9]{2}-[0-9]{2})\([월화수목금토일]\) ([0-9]{2}:[0-9]{2}) \([0-9](?:시간|박)\)[\r\n]+(도보방문|차량방문)""".toRegex()

            msg?.let { it1 ->
                val matchResult = regex.find(it1)
                val groupValues = matchResult?.groupValues
                if(!groupValues.isNullOrEmpty()) {
                    Log.d("MmsReceiver", "$groupValues")

                    val reservationType = groupValues[1]
                    Log.d("MmsReceiver", "reservationType : $reservationType")

                    val store = groupValues[2]
                    Log.d("MmsReceiver", "store: $store")

                    val reservationNo = groupValues[3]
                    Log.d("MmsReceiver", "reservationNo: $reservationNo")

                    val roomType = groupValues[4]
                    Log.d("MmsReceiver", "roomType: $roomType")

                    val price = groupValues[5]
                    Log.d("MmsReceiver", "price: $price")

                    val reserveerName = groupValues[6]
                    Log.d("MmsReceiver", "reserveerName: $reserveerName")

                    val reserveerContact = groupValues[7]
                    Log.d("MmsReceiver", "reserveerContact: $reserveerContact")

                    val enterDate = groupValues[8]
                    Log.d("MmsReceiver", "enterDate: $enterDate")

                    val enterTime = groupValues[9]
                    Log.d("MmsReceiver", "enterTime: $enterTime")

                    val checkoutDate = groupValues[10]
                    Log.d("MmsReceiver", "checkoutDate: $checkoutDate")

                    val checkoutTime = groupValues[11]
                    Log.d("MmsReceiver", "checkoutTime: $checkoutTime")

                    val transportationType = groupValues[12]
                    Log.d("MmsReceiver", "transportationType: $transportationType")

                }

            }

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