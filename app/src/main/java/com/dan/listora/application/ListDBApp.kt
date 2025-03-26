package com.dan.listora.application

import android.app.Application
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.ListDB

class ListDBApp: Application()  {
    private val database by lazy {
        ListDB.getDatabase(this)
    }
    val repository by lazy {

        ListRepository(database.listDao())
    }
}