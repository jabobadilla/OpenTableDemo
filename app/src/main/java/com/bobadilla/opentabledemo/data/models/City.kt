package com.bobadilla.opentabledemo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CitiesArray (
    @SerializedName("cities") val cities : ArrayList<String>
)

@Entity
data class City (
    @Expose(serialize = false, deserialize = false)
    @PrimaryKey(autoGenerate = true) var id : Int = 0,
    var city: String = ""
)