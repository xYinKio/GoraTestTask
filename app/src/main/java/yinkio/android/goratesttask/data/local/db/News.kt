package yinkio.android.goratesttask.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class News(
    @PrimaryKey
    val url: String = "",
    val category: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: Long = 0,
)
