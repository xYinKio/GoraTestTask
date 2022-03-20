package yinkio.android.goratesttask.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(news: List<News>)

    @Query("SELECT * FROM News WHERE category = :category")
    fun read(category: String) : List<News>



    @Query("DELETE FROM News WHERE url = :url")
    fun delete(url: String)
}