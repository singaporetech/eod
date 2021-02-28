package com.boliao.eod

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * The repo layer that manages potentially multiple sources of data.
 * - recommended best practice for SWA
 * - most common use case is to handle an online + offline (cached) DB
 * - only need to pass in the DAO and not the Room DB which is encapsulated away
 */
class PlayerRepo(private val playerDAO: PlayerDAO) {

    /**
     * Wrapper for inserting into the database.
     * - can explicitly specify for this to be done on a WorkerThread
     * - suspend fun to facilitate it being an async call on a background thread
     *
     * @param player the player record to insert
     */
    @SuppressWarnings("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(player: Player) {
        playerDAO.insert(player)
    }

    /**
     * Get a list of players by the name.
     * @param name of the player record to retrieve
     * TODO actually only one record since it is id...
     */
    suspend fun getPlayer(name:String) : List<Player> {
        return playerDAO.getByName(name)
    }

    /**
     * Check if db contains this name.
     * @param name the name to find
     * @return Boolean of whether the name exists or not in the db
     */
    fun contains(name:String): Boolean {
        return playerDAO.getByName(name).isNotEmpty()
    }

    /**
     * TODO SERVICES 2.5: Add password updating functionality to the db.
     *
     * Update a record with the password.
     * @param name the name id to be updated
     * @param pw the pw to update the record
     */
    suspend fun updatePw(name: String, pw: String) {
        playerDAO.updatePw(name, pw)
    }

}