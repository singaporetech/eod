package com.boliao.eod

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object Interface for Player records.
 * This DAO interface is used to generate a clean code-based API for your DB.
 * In other words associate SQL queries to methods calls.
 * - similarly uses kotlin annotations to create loads of boilerplate code
 */
@Dao // tells Room this is part of that
interface PlayerDAO {

    // the insert annotation doesn't even need proper SQL
    // .Ignore will ignore a new player if it has the same key
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(player: Player)

    @Query("DELETE FROM player_table")
    fun purge()

    @Query("SELECT * FROM player_table ORDER BY name ASC")
    fun getOrderedPlayerNames(): List<Player>

    @Query("SELECT * FROM player_table WHERE name = :name")
    fun getByName(name:String): List<Player>

    // TODO SERVICES 2.4: query to update pw in a player record
    // 1. create a query to UPDATE existing records by SETting pw WHERE name matches
    @Query("UPDATE player_table SET pw = :pw WHERE name = :name")
    fun updatePw(name: String, pw: String)
}