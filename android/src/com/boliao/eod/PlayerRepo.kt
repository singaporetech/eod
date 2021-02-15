package com.boliao.eod

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * TODO ARCH 3.5: Manage membership data with a Room
 * The repo layer that manages potentially multiple sources of data.
 * - recommended best practice for SWA
 * - most common use case is to handle an online + offline (cached) DB
 * - only need to pass in the DAO and not the Room DB which is encapsulated away
 */
class PlayerRepo(private val playerDAO: PlayerDAO) {

    // a streaming LiveData that allows observers to get updated data when there are changes
    // - note that Room executes all queries on a separate thread
    val allPlayers: Flow<List<Player>> = playerDAO.getOrderedPlayerNamesFlow()

    /**
     * Wrapper for inserting into the database.
     * - can explicitly specify for this to be done on a WorkerThread
     * - suspend fun to facilitate it being an async call on a background thread
     */
    @SuppressWarnings("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(player: Player) {
        playerDAO.insert(player)
    }

    /**
     * Wrapper for getting records by name.
     */
    suspend fun contains(name:String): Boolean {
        return playerDAO.getByName(name).isNotEmpty()
    }

}