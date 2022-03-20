package yinkio.android.goratesttask.data.remote

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import yinkio.android.goratesttask.data.getCategories
import yinkio.android.goratesttask.data.remote.pojo.news.Article
import yinkio.android.goratesttask.data.remote.pojo.news.NewsResult
import yinkio.android.goratesttask.domain.Repository
import yinkio.android.goratesttask.ui.items.CategoryItem
import yinkio.android.goratesttask.ui.items.NewsItem
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://newsapi.org"
private const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"


@Singleton
class RemoteSource @Inject constructor() : Repository {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(NewsApiService::class.java)

    override suspend fun getNews(category: Category): List<NewsItem> {
       return getNewsCatching {
           service.getNews(category.name).awaitResponse()
       }
    }



    @SuppressLint("SimpleDateFormat")
    private fun date(it: Article) : Long {
        return try {
            SimpleDateFormat(DATE_PATTERN).parse(it.publishedAt!!)!!.time
        }catch (ex: Exception){
            0L
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getNewsForAllCategories(): List<CategoryItem> {
        return getCategories { getNews(it) }
    }

    private suspend fun getNewsCatching(getNews: suspend () -> Response<NewsResult>) : List<NewsItem>{
        try {
            val res = getNews()

            return if (res.isSuccessful) {
                res.body()?.articles?.map {
                    NewsItem(
                        title = it.title ?: "",
                        url = it.url ?: "",
                        imageUrl = it.urlToImage ?: "",
                        date = date(it),
                        description = it.description?: ""
                    )
                } ?: listOf()
            } else {
                listOf()
            }
        } catch (ex: Exception){
            return emptyList()
        }
    }
}