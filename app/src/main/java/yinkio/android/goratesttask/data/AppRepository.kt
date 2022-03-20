package yinkio.android.goratesttask.data

import android.content.Context
import android.net.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import yinkio.android.goratesttask.data.local.db.News
import yinkio.android.goratesttask.data.local.db.NewsCache
import yinkio.android.goratesttask.data.remote.RemoteSource
import yinkio.android.goratesttask.data.remote.Category
import yinkio.android.goratesttask.domain.Repository
import yinkio.android.goratesttask.ui.items.CategoryItem
import yinkio.android.goratesttask.ui.items.NewsItem
import javax.inject.Inject

@ViewModelScoped
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteSource: RemoteSource,
    private val cache: NewsCache,
) : Repository {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getNews(category: Category): List<NewsItem> {
        return if (hasInternet()){
            val news = remoteSource.getNews(category)
            if (news.isNotEmpty()){
                saveNews(news, category)
                news
            } else {
                cache.getNews(category)
            }

        } else {
            cache.getNews(category)
        }.sortedBy { -it.date }
    }

    private fun saveNews(
        news: List<NewsItem>,
        category: Category
    ) {
        coroutineScope.launch {
            cache.saveNews(news.map {
                News(
                    url = it.url,
                    category = category.name,
                    title = it.title,
                    imageUrl = it.imageUrl,
                    date = it.date,
                    description = it.description
                )
            })
        }
    }

    private fun hasInternet() : Boolean{
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo?.isConnected == true
    }

    override suspend fun getNewsForAllCategories(): List<CategoryItem> {
        return getCategories { getNews(it) }
    }
}