package com.dan.listora.application

import android.app.Application
import androidx.room.Database
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.ListDataBase
import com.dan.listora.data.db.IngredientDataBase
import com.dan.listora.data.db.model.IngEntity

class ListDBApp : Application() {

    val database by lazy {
        ListDataBase.getDatabase(this)
    }

    val ingredientDatabase by lazy {
        IngredientDataBase.getDatabase(this)
    }

    val repository by lazy {
        ListRepository(database.listDao())
    }
}
