package com.dan.listora.application

import android.app.Application
import com.dan.listora.data.IngredientRepository
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.ListDataBase

class ListDBApp : Application() {

    val database by lazy {
        ListDataBase.getDatabase(this)
    }

    val repository by lazy {
        ListRepository(database.listDao(), database.ingredientDAO())
    }

    val ingredientRepository by lazy {
        IngredientRepository(database.ingredientDAO())
    }
}

