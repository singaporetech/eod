package com.boliao.eod

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * To represent a single player record.
 * - uses kotlin specific annotations extensively (kapt) to correspond to the
 *   names in SQLite table
 */
@Entity(tableName = "player_table")
data class Player(
        @PrimaryKey @ColumnInfo(name = "name")
        val name: String,

        // you can specify the name using @ColumnInfo, otherwise default is fine
        // @ColumnInfo(name = "pw")
        val pw: String
        )