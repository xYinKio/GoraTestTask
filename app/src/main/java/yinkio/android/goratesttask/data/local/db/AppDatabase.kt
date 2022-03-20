package yinkio.android.goratesttask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        News::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao() : NewsDao

}