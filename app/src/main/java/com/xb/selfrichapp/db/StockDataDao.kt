package com.xb.selfrichapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

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
 * date        : 2026/5/9 15:12
 * description :
 */
@Dao
interface StockDataDao {
    @Query("select * from action_record order by _id desc")
    fun findAll(): List<StockRecordEntity>

    @Query("select * from action_record where action_time>=:min and action_time <=:max")
    fun findRecordByTimeArea(min: Long,max: Long): List<StockRecordEntity>

    @Query("select * from action_record where action_time>=:min and action_time <=:max and `offset`>0")
    fun findRecordWin(min: Long,max: Long): List<StockRecordEntity>

    @Query("select * from action_record where action_time>=:min and action_time <=:max and `offset`<0")
    fun findRecordLose(min: Long,max: Long): List<StockRecordEntity>

    @Query("select * from action_record where mode=0 and action_time>=:min and action_time <=:max and `offset`>0")
    fun findRecordWinMode(min: Long,max: Long): List<StockRecordEntity>

    @Query("select * from action_record where mode=0 and action_time>=:min and action_time <=:max and `offset`<0")
    fun findRecordLoseMode(min: Long,max: Long): List<StockRecordEntity>
    @Insert
    fun add(data: List<StockRecordEntity>)
}