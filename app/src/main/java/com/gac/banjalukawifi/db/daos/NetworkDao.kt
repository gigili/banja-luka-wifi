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

    @Query("SELECT * FROM networks ORDER BY name ASC")
    fun getAll() : List<Network>

    @Query("SELECT * FROM networks WHERE id = :id")
    fun get(id : Int) : Network

    @Query("DELETE FROM networks")
    fun deleteAll()

    @Query("SELECT * FROM networks WHERE name LIKE :term ORDER BY name ASC")
    fun findByName(term: String): List<Network>
}