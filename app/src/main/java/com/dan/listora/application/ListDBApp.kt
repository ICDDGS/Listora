package com.dan.listora.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.dan.listora.data.IngredientRepository
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.ListDataBase
import com.dan.listora.data.HistorialRepository


class ListDBApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }


    val database by lazy {
        ListDataBase.getDatabase(this)
    }

    val repository by lazy {
        ListRepository(database.listDao(), database.ingredientDAO())
    }

    val ingredientRepository by lazy {
        IngredientRepository(database.ingredientDAO())
    }
    val historialRepository by lazy {
        HistorialRepository(database.historialDao())
    }

}

