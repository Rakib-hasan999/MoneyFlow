package com.moneyflow.app

import android.app.Application
import androidx.room.Room
import com.moneyflow.app.data.AppDatabase
import com.moneyflow.app.repository.MoneyFlowRepository

class MoneyFlowApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "moneyflow.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    val repository: MoneyFlowRepository by lazy {
        MoneyFlowRepository(database.projectDao(), database.transactionDao())
    }
}
