package yinkio.android.goratesttask.data.local.db

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import yinkio.android.goratesttask.data.getCategories
import yinkio.android.goratesttask.data.remote.Category
import yinkio.android.goratesttask.domain.Repository
import yinkio.android.goratesttask.ui.items.CategoryItem
import yinkio.android.goratesttask.ui.items.NewsItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsCache @Inject constructor(
    @ApplicationContext private val context: Context
) : Repository {

    private val db = Room.databaseBuilder(context,
        AppDatabase::class.java,
        "app_database"
    ).build()

    override suspend fun getNews(category: Category): List<NewsItem> {
        return db.newsDao().read(category.name).map {
            NewsItem(
                title = it.title,
                url = it.url,
                imageUrl = it.imageUrl,
                date = it.date,
                description = it.description
            )
        }
    }

    override suspend fun getNewsForAllCategories(): List<CategoryItem> {
        return getCategories { getNews(it) }
    }

    suspend fun saveNews(news: List<News>){
        db.newsDao().create(news)
    }
}