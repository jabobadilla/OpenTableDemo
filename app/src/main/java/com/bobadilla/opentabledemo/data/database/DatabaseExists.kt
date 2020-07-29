package com.bobadilla.opentabledemo.data.database

import android.content.Context
import com.bobadilla.opentabledemo.R
import java.io.File

object DatabaseExists {

    fun databaseFileExists(context: Context) : Boolean {
        return try {
            File(context.getDatabasePath(context.resources.getString(R.string.db_name)).absolutePath).exists()
        }
        catch (e: Exception) {
            false
        }
    }

}