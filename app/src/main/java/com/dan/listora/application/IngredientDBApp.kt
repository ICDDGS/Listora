package com.dan.listora.application

import android.app.Application
import com.dan.listora.data.IngredientRepository
import com.dan.listora.data.db.IngredientDataBase


class IngredientDBApp:Application() {
    private val database by lazy {
        IngredientDataBase.getDatabase(this@IngredientDBApp)
    }
    val repository by lazy {
        IngredientRepository(database.ingredientDAO())
    }
}

