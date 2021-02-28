package com.boliao.eod

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * A player entity to represent a single player record.
 * - uses kotlin specific annotations extensively (kapt) to correspond to the
 *   names in SQLite table
 */
@Entity(tableName = "player_table")
data class Player(
        @PrimaryKey @ColumnInfo(name = "name")
        val name: String,

        val age: Int?,

        // TODO SERVICES 2.3: Add a pw field
        // you can specify the name using @ColumnInfo, otherwise default is fine
         @ColumnInfo(name = "pw")
         val pw: String?

        )