package yinkio.android.goratesttask.data.remote.pojo.news

data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceData?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)