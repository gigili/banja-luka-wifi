package com.gac.banjalukawifi.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "networks")
class Network(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, val name: String = "", val password: String = "", val address: String? = null,
    val geoLat: String? = null, val geoLong: String? = null, val userID: String? = null
) {

    override fun toString(): String {
        return "${this.name} \n${this.address}"
    }
}