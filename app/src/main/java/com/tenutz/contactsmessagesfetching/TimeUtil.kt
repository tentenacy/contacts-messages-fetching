package com.tenutz.contactsmessagesfetching

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertDateToTimestamp(date: String): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    Log.d("TTTT Time -> ", sdf.parse(date).time.toString())
    Log.d("TTT Unix -> ", (System.currentTimeMillis()).toString())
    return sdf.parse(date).time
}

@SuppressLint("SimpleDateFormat")
fun convertTimestampToDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = sdf.format(timestamp)
    Log.d("TTT UNix Date -> ", sdf.format((System.currentTimeMillis())).toString())
    Log.d("TTTT date -> ", date.toString())
    return date
}