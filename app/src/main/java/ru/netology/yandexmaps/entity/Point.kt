package ru.netology.yandexmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points")
data class Point(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    var title: String,
    var description: String
)
