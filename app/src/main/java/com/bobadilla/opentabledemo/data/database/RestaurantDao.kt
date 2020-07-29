package com.bobadilla.opentabledemo.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant

/*
A DAO is basically an api to access required data from your database. It has two sole purposes:

It saves you from writing direct queries, which are more error-prone and harder to debug.
It isolates query logic from database creation and migration code for better manageability.
 */

@Dao
interface RestaurantDao {
    // 1: Select All cities
    @Query("SELECT * FROM City ORDER BY city ASC")
    fun getAllCitites() : LiveData<List<City>>

    // 2: Select by city
    @Query("SELECT * FROM City WHERE city LIKE '%' || :cityName || '%'")
    fun findBy(cityName: String) : LiveData<List<City>>

    // 3: Select restaurants by city
    @Query("SELECT * FROM Restaurant WHERE city = :city")
    fun findByCity(city: String) : LiveData<List<Restaurant>>

    // 4: Select by restaurant name in city
    @Query("SELECT * FROM Restaurant WHERE name LIKE '%%' || :name || '%' and city = :city")
    fun findByRestaurantName(name: String, city: String) : LiveData<List<Restaurant>>

    // 5: Select by Id
    @Query("SELECT * FROM Restaurant WHERE id = :id")
    fun findByID(id: String) : LiveData<Restaurant>

    // 4: Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(restaurant: Restaurant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: City)

    // 5: Delete
    @Query("DELETE FROM City")
    fun deleteAllCities()

    @Query("DELETE FROM Restaurant")
    fun deleteAllRestaurants()

    @Query("SELECT COUNT(*) FROM Restaurant WHERE city = :city")
    fun countRestaurantsInCity(city: String) : Int

}