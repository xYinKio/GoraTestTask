package yinkio.android.goratesttask.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import dagger.hilt.android.AndroidEntryPoint
import yinkio.android.goratesttask.R
import yinkio.android.goratesttask.databinding.ActivityMainBinding
import yinkio.android.goratesttask.databinding.ListItemCategoryBinding
import yinkio.android.goratesttask.databinding.ListItemNewsBinding
import yinkio.android.goratesttask.data.local.files.ImageCache.loadImage
import yinkio.android.goratesttask.ui.extensions.*
import yinkio.android.goratesttask.ui.items.CategoryItem
import yinkio.android.goratesttask.ui.items.NewsItem

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val adapter by lazy { categoryAdapter() }
    private val viewModel: NewsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        binding.apply {
            recycler.adapter = adapter
            searchView.doOnTextChange { viewModel.filterByTitleAndDescription(it) }
            val searchText = searchView.findViewById<View>(R.id.search_plate)
            searchText.background = null
        }

        setContentView(binding.root)

        viewModel.apply {
            observe(news){ adapter.submitList(it) }
        }
    }


    private fun categoryAdapter() = recyclerAdapter<CategoryItem, ListItemCategoryBinding>(
        onBind = { item, holder ->
            if (holder.state == null){
                holder.state = itemAdapter()
            }
            val adapter =  holder.state as ListAdapter<NewsItem, *>


            recycler.adapter = adapter
            adapter.submitList(item.news)
            name.text = item.category.locale(this@MainActivity)

        },
        areItemsTheSame = {old, new -> old.category == new.category}
    )

    private fun itemAdapter()
    : ListAdapter<NewsItem, ItemViewHolder<NewsItem, ListItemNewsBinding>> {
        return recyclerAdapter(
            onBind = { item, _ ->
                title.text = item.title


                loadImage(image, item.imageUrl)
//                loadImage(item)

                root.setOnClickListener { openNewsInBrowser(item) }
            },
            areItemsTheSame = { old, new -> old.url == new.url },
        )
    }
//
//    private fun ListItemNewsBinding.loadImage(item: NewsItem) {
//        Glide.with(this@MainActivity)
//            .load(item.imageUrl)
//            .into(image)
//    }

    private fun openNewsInBrowser(item: NewsItem) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(item.url)
        startActivity(intent)
    }

}