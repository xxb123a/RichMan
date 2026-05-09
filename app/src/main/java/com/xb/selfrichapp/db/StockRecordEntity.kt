package com.xb.selfrichapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
 * date        : 2026/5/9 15:11
 * description :
 */
@Entity("action_record")
class StockRecordEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0

    @ColumnInfo(name = "action_time")
    var modify_time = 0L
    @ColumnInfo(name = "name")
    var name = ""
    @ColumnInfo(name = "mode")
    var mode = 0
    @ColumnInfo(name = "buy")
    var buy = 0.0
    @ColumnInfo(name = "sale")
    var sale = 0.0
    @ColumnInfo(name = "offset")
    var offset = 0.0
}