package com.chopin.marketmanager.util

import android.app.Activity
import android.util.Log
import android.widget.Toast


fun Activity.toast(msg:String){
    Toast.makeText(applicationContext,msg, Toast.LENGTH_LONG).show()
}

fun Any.i(msg:String){
    Log.i("chopin",msg)
}