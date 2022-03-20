package yinkio.android.goratesttask.ui.extensions

import android.content.Context
import yinkio.android.goratesttask.R
import yinkio.android.goratesttask.data.remote.Category

fun Category.locale(context: Context) : String{
    val resId = when(this){
        Category.business -> R.string.business
        Category.entertainment -> R.string.entertainment
        Category.general -> R.string.general
        Category.health -> R.string.health
        Category.science -> R.string.science
        Category.sports -> R.string.sports
        Category.technology -> R.string.technology
    }

    return context.getString(resId)
}