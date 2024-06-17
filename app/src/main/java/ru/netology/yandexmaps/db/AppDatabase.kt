package ru.netology.yandexmaps.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.yandexmaps.dao.PointDao
import ru.netology.yandexmaps.entity.Point

@Database(entities = [Point::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pointDao(): PointDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "points.db"
            ).build()
    }
}
