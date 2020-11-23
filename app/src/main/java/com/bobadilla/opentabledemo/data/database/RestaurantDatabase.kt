package com.bobadilla.opentabledemo.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.connection.api.APIRetriever
import com.bobadilla.opentabledemo.data.dataLoad.DataNetLoad
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [City::class, Restaurant::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun restaurantDao() : RestaurantDao

    companion object {
        private val lock = Any()
        private var DB_NAME : String? = null
        private var INSTANCE: RestaurantDatabase? = null

        fun prePopulateCities(database: RestaurantDatabase, citiesList: MutableList<City>) {
            for (city in citiesList) {
                    database.restaurantDao().insertCity(city)
            }
        }

        fun getInstance(application: Application) : RestaurantDatabase {
            this.DB_NAME = application.resources.getString(R.string.db_name)
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(application, RestaurantDatabase::class.java, this.DB_NAME!!)
                        .allowMainThreadQueries()
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                INSTANCE?.let { database ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                        //val data = DataNetLoad.loadCities(true).await()
                                        val data = APIRetriever().getAPICities(true)
                                        prePopulateCities(database,data)
                                    }
                                }
                            }
                        })
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}