package ru.netology.yandexmaps.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.netology.yandexmaps.entity.Point

@Dao
interface PointDao {
    @Insert
    suspend fun insert(point: Point): Long

    @Update
    suspend fun update(point: Point)

    @Delete
    suspend fun delete(point: Point)

    @Query("SELECT * FROM points")
    suspend fun getAllPoints(): List<Point>
}
