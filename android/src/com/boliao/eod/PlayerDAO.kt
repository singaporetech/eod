package com.boliao.eod

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object Interface for Player records.
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
    @Query("SELECT * FROM player_table ORDER BY name ASC")
    fun getOrderedPlayerNamesFlow(): Flow<List<Player>>
}