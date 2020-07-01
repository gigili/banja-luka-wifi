package com.gac.banjalukawifi.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "networks")
class Network(
    var name: String = "", var password: String = "", var address: String? = null,
    var geoLat: String? = null, var geoLong: String? = null, var userID: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    
    fun setID(value : Int){
        id = value
    }

    override fun toString(): String {
        return "${this.name} | ${this.password}"
    }
}