package yinkio.android.goratesttask.ui.items

import yinkio.android.goratesttask.data.remote.Category

data class CategoryItem(
    val category: Category,
    val news: List<NewsItem>
)
