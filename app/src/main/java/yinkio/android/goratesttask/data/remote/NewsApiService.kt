package yinkio.android.goratesttask.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import yinkio.android.goratesttask.data.remote.pojo.news.NewsResult

private const val KEY_HEADER = "X-Api-Key: e5888c7ed2fe4861812bba23dd63f4dc"
private const val PATH = "v2/top-headlines"
private const val CATEGORY = "category"

interface NewsApiService {

    @Headers(KEY_HEADER)
    @GET(PATH)
    fun getNews(@Query(CATEGORY) category: String) : Call<NewsResult>
}