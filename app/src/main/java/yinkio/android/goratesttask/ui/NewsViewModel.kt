package yinkio.android.goratesttask.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import yinkio.android.goratesttask.domain.Repository
import yinkio.android.goratesttask.ui.items.CategoryItem
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    private val _news = MutableStateFlow<List<CategoryItem>>(listOf())
    val news get() = _news.asSharedFlow()

    private var buffer = listOf<CategoryItem>()

    init {
        onIO {
            _news.emit(repository.getNewsForAllCategories())
            buffer = _news.value
        }

    }

    fun filterByTitleAndDescription(text: String) = onIO{
        if (text.isEmpty()) _news.emit(buffer)

        val categoryItems = mutableListOf<CategoryItem>()
        buffer.forEach { categoryItem ->
            val filtered = categoryItem.news.filter {
                it.title.lowercase().contains(text.lowercase())
                || it.description.lowercase().contains(text.lowercase())
            }
            if (filtered.isNotEmpty()){
                categoryItems.add(CategoryItem(categoryItem.category, filtered))
            }
        }

        _news.emit(categoryItems)
    }

    private inline fun onIO(crossinline block: suspend () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }
}