package com.chopin.marketmanager.sql

import android.content.Context
import android.content.ContextWrapper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import java.io.File

class DatabaseContext(context: Context?) : ContextWrapper(context) {

    val mDirPath: String = Environment.getExternalStorageDirectory().absolutePath


    override fun getDatabasePath(name: String): File {
        val result = File(mDirPath + File.separator + name)
        if (!result.parentFile.exists()) {
            result.parentFile.mkdirs()
        }
        return result
    }

    override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory)
    }

    override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory, errorHandler: DatabaseErrorHandler): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).absolutePath, factory, errorHandler)
    }

}
