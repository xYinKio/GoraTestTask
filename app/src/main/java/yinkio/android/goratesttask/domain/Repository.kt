package yinkio.android.goratesttask.domain

import yinkio.android.goratesttask.data.remote.Category
import yinkio.android.goratesttask.ui.items.CategoryItem
import yinkio.android.goratesttask.ui.items.NewsItem

interface Repository {

    suspend fun getNews(category: Category) : List<NewsItem>

    suspend fun getNewsForAllCategories() : List<CategoryItem>
}