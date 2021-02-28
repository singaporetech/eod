package com.boliao.eod

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * TODO ARCH 3.3: Manage membership data with a Room
 * Create a Data Access Object Interface for Player records.
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

    // TODO THREADING:
    // 1. use a Flow component to expose a LiveData stream from the DB
    // NOTE that Flow is a way to handle live continuous data
    // - Flow is like thread-safe LiveData
    // - emit and collect is like postValue and observe
    @Query("SELECT * FROM player_table ORDER BY name ASC")
    fun getOrderedPlayerNamesFlow(): Flow<List<Player>>

    // TODO ARCH 3
    // 1. select a particular ID
    @Query("SELECT * FROM player_table WHERE name = :name")
    fun getByName(name:String): List<Player>

    // TODO SERVICES 2.4
    // 1. create a query to UPDATE existing records by SETting pw WHERE name matches
    @Query("UPDATE player_table SET pw = :pw WHERE name = :name")
    fun updatePw(name: String, pw: String)
}