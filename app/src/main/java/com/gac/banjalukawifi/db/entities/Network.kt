package com.gac.banjalukawifi.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "networks")
class Network(
    val name: String = "", val password: String = "", val address: String? = null,
    val geoLat: String? = null, val geoLong: String? = null, val userID: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    
    fun setID(value : Int){
        id = value
    }

    override fun toString(): String {
        return "${this.name} \n${this.address}"
    }
}