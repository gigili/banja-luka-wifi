package com.gac.banjalukawifi.db.daos

import androidx.room.*
import com.gac.banjalukawifi.db.entities.Network

@Dao
interface NetworkDao {

    @Insert
    fun insert(network : Network)

    @Update
    fun update(network: Network)

    @Delete
    fun delete(network: Network)

    @Query("SELECT * FROM networks")
    fun getAll() : List<Network>

    @Query("SELECT * FROM networks WHERE id = :id")
    fun get(id : Int) : Network

    @Query("DELETE FROM networks")
    fun deleteAll()
}