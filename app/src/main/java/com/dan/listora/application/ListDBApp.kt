package com.dan.listora.application

import android.app.Application
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.ListDataBase


class ListDBApp: Application()  {
    private val database by lazy {
        ListDataBase.getDatabase(this@ListDBApp)
    }
    val repository by lazy {
        ListRepository(database.listDao())
    }

}