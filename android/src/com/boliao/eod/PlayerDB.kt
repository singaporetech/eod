package com.boliao.eod

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Player Room DB
 * - a clever layer on top of SQLite DB
 * - we used to extend SQLiteOpenHelper and implement the handlers ourselves
 * - Room handles all the boilerplate now
 * - all queries are automatically handled async on a background thread
 * - normally need to maintain just one instance (i.e., singleton)
 * - exportSchema will be useful in a real app when you want to commit your
 *   schema into VCS
 * - need to define a migration strategy when the version is bumped in a real app
 */
@Database(entities = arrayOf(Player::class), version = 1, exportSchema = false)
public abstract class PlayerDB: RoomDatabase() {

    // expose an abstract getter function for the DAO
    abstract fun playerDAO(): PlayerDAO

    companion object {
        // make this a singleton
        @Volatile
        private var INSTANCE: PlayerDB? = null

        /**
         * Singleton method to get single DB instance.
         * Return INSTANCE if not null, else create/store it and return
         */
        fun getDatabase(context: Context): PlayerDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlayerDB::class.java,
                        "player_db"
                )
                        .fallbackToDestructiveMigration()
                        .build()

                // store static ref to instance
                INSTANCE = instance

                // return instance
                instance
            }
        }
    }
}