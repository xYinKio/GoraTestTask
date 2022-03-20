package yinkio.android.goratesttask.data.local.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object ImageCache{

    private val imageCoroutines = mutableMapOf<ImageView, CoroutineScope>()

    fun loadImage(
        imageView: ImageView,
        url: String,
        onLoadingDrawRes: Int = android.R.drawable.stat_sys_download,
        onErrorDrawRes: Int = android.R.drawable.stat_notify_error
    ) {
        imageView.setImageDrawable(drawable(imageView.context, onLoadingDrawRes))
        updateScope(imageView).launch {
            loadAsync(imageView, url, onErrorDrawRes)
            imageCoroutines.remove(imageView)
        }
    }

    private fun updateScope(imageView: ImageView): CoroutineScope {
        imageCoroutines[imageView]?.cancel()
        val scope = CoroutineScope(Dispatchers.IO)
        imageCoroutines[imageView] = scope
        return scope
    }

    private suspend fun loadAsync(
        imageView: ImageView,
        url: String,
        onErrorDrawRes: Int
    ) {
        when (val res = load(imageView.context, url)) {
            is State.Success -> {
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(res.bitmap)
                }
            }
            is State.Error -> {
                withContext(Dispatchers.Main) {
                    imageView.setImageDrawable(
                        drawable(imageView.context, onErrorDrawRes)
                    )
                }
                Log.d("ImageCache", res.message)
            }
        }
    }

    private fun drawable(
        context: Context,
        drawRes: Int
    ) = ContextCompat.getDrawable(context, drawRes)

    private fun load(context: Context ,url: String) : State {
        val res = when(val localState = loadFromLocal(context, url)){
            is State.Success -> {
                return localState }
            is State.Error -> {
                loadFromRemote(context, url) }
        }

        return when (res){
            is State.Success -> {
                saveImage(context, url, res.bitmap)
                res
            }
            is State.Error -> { res }
        }

    }

    private fun saveImage(
        context: Context,
        url: String,
        bitmap: Bitmap
    ) {
        CoroutineScope(Dispatchers.IO).launch{
            runCatching {
                val out = FileOutputStream(context.cacheDir.absolutePath + "/${url.hashCode()}")
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }
    }

    private fun loadFromLocal(context: Context, url: String) : State {
        val path = context.cacheDir.absolutePath  + "/${url.hashCode()}"
        val file = File(path)
        if (!file.exists()) return State.Error("File does not exists for $url")

        val inputStream = FileInputStream(file)
        val bitmap = BitmapFactory.decodeStream(inputStream)


        return State.Success(bitmap)
    }




    private fun loadFromRemote(context: Context, url: String) : State {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo == null || !cm.activeNetworkInfo!!.isConnected) {
            return State.Error("No internet connection")
        }

        val request = request(url)
        if (request.isFailure) return State.Error(request.exceptionOrNull()?.message + ": $url")

        val response = response(request)
        if (response.isFailure) return State.Error("${response.exceptionOrNull()?.message}")


        if (response.getOrNull()!!.isSuccessful) {
            val inputStream = response.getOrNull()!!.body()?.byteStream()
                ?: return State.Error("Empty response body, code: ${response.getOrNull()!!.code()}")
            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: return State.Error("Bitmap is null")
            return State.Success(bitmap)
        } else {
            return State.Error("Unsuccessful. Code: ${response.getOrNull()!!.code()}")
        }
    }

    private fun response(request: Result<Request>) = runCatching {
        OkHttpClient()
            .newCall(request.getOrNull()!!)
            .execute()
    }

    private fun request(url: String) = runCatching {
        Request.Builder()
            .url(url)
            .build()
    }

    private sealed class State{
        data class Success(val bitmap: Bitmap) : State()
        data class Error(val message: String) : State()
    }

}


