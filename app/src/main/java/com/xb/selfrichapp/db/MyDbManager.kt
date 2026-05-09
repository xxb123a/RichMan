package com.xb.selfrichapp.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xb.selfrichapp.TheApp

/**
 *_    .--,       .--,
 *_   ( (  \\.---./  ) )
 *_    '.__/o   o\\__.'
 *_       {=  ^  =}
 *_        >  -  <
 *_       /       \\
 *_      //       \\\\
 *_     //|   .   |\\\\
 *_     \"'\\       /'\"_.-~^`'-.
 *_        \\  _  /--'         `
 *_      ___)( )(___
 *_     (((__) (__)))
 * author      : xue
 * date        : 2026/5/9 15:10
 * description :
 */
@Database(
    entities = [StockRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb: RoomDatabase(){
    abstract fun actionDao(): StockDataDao
}

object MyDbManager {
    val db by lazy {
        Room.databaseBuilder(TheApp.mInstance, AppDb::class.java,"rich_main.db")
            .allowMainThreadQueries()
            .build()
    }
}

fun fetchStockDao() = MyDbManager.db.actionDao()