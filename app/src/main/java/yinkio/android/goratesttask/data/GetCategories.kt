package yinkio.android.goratesttask.data

import yinkio.android.goratesttask.data.remote.Category
import yinkio.android.goratesttask.ui.items.CategoryItem
import yinkio.android.goratesttask.ui.items.NewsItem

internal suspend fun getCategories(
    block: suspend (category: Category) -> List<NewsItem>
) : List<CategoryItem>{

    val categories = mutableListOf<CategoryItem>()

    Category.values().forEach {
        categories.add(CategoryItem(Category.valueOf(it.name), block(it)))
    }

    return categories.sortedBy { it.category }
}