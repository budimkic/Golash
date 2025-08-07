package com.golash.app.data.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String = list?.joinToString(",") ?: ""

    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isNullOrEmpty()) emptyList() else data.split(",")

}