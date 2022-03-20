package yinkio.android.goratesttask.data.remote.pojo.news

data class NewsResult(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)