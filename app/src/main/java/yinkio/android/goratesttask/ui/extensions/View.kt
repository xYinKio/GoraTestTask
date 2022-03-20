package yinkio.android.goratesttask.ui.extensions

import androidx.appcompat.widget.SearchView

fun SearchView.doOnTextChange(action: (String) -> Unit){
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }
        override fun onQueryTextChange(newText: String?): Boolean {
            action(newText.toString())
            return true
        }
    })
}
