package ru.netology.yandexmaps.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.netology.yandexmaps.entity.Point

@Dao
interface PointDao {
    @Query("SELECT * FROM points")
    fun getAllPoints(): LiveData<List<Point>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: Point)

    @Update
    suspend fun updatePoint(point: Point)

    @Delete
    suspend fun deletePoint(point: Point)
}
