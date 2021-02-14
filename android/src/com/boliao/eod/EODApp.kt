package com.boliao.eod

import android.app.Application

class EODApp: Application() {
    // TODO ARCH 3: Manage membership data with a Room
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    // This should be done at the application level in
    val db by lazy { PlayerDB.getDatabase(this) }
    val repo by lazy { PlayerRepo(db.playerDAO())}
}