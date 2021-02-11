package com.github.dromanenko.images

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.URL


class MainActivity : AppCompatActivity() {
    companion object {
        private const val HOST = "https://www.flickr.com/services/rest/?method=flickr.photos.search"
        private const val API_KEY = "c4aa654412134d7b78e1b4b13c2dbdef"
        private const val TEXT = "ITMO"
        private const val PRIVACY_FILTER = "1"
        private const val MEDIA = "photos"
        private const val FORMAT = "json"
        private const val NOJSONCALLBACK = "1"
        private const val KEY_QUERY = "key_query"
        private const val KEY_LIST = "key_list"
        private const val TOKEN_DOWNLOAD_JSON = "download_JSON"
    }

    data class Photo(
        val id: String?,
        var owner: String?,
        var secret: String?,
        var server: String?,
        var farm: String?,
        var title: String?,
        var ispublic: String?,
        var isfriend: String?,
        var isfamily: String?
    )

    data class Photos(
        var page: String?,
        var pages: String?,
        var perpage: String?,
        var total: String?,
        var photo: List<Photo>
    )

    data class JSONFlickr(var photos: Photos?, var stat: String?)

    private var imagesList: List<Image>? = null
    private var query: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(KEY_LIST, imagesList as ArrayList<out Parcelable?>?)
        outState.putString(KEY_QUERY, query)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imagesList = savedInstanceState.getParcelableArrayList(KEY_LIST)
        query = savedInstanceState.getString(KEY_QUERY)
    }

    override fun onResume() {
        super.onResume()

        if (query == null) {
            sw.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    makeQuery(query)
                    this@MainActivity.query = query
                    sw.visibility = View.GONE
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return true
                }
            })
        } else {
            sw.visibility = View.GONE
            makeQuery(query!!)
        }
    }

    private fun makeQuery(query: String) {
        imagesList?.let { afterDownload(it) }
            ?: JSONDownloader(this).execute(
                HOST,
                API_KEY,
                query,
                PRIVACY_FILTER,
                MEDIA,
                FORMAT,
                NOJSONCALLBACK
            )
    }

    private class JSONDownloader(activity: MainActivity) :
        AsyncTask<String, Int, List<Image>>() {
        private val wr = WeakReference(activity)

        override fun doInBackground(vararg params: String): List<Image> {
            val imagesList = mutableListOf<Image>()
            val url =
                "${params[0]}&api_key=${params[1]}&text=${params[2]}&privacy_filter=${params[3]}&media=${params[4]}&format=${params[5]}&nojsoncallback=${params[6]}"
            try {
                Log.i(TOKEN_DOWNLOAD_JSON, "Download from $url")
                InputStreamReader(
                    URL(url)
                        .openConnection()
                        .getInputStream()
                ).use {
                    Gson().fromJson<JSONFlickr>(
                        it.readText(),
                        object : TypeToken<JSONFlickr>() {}.type
                    ).photos?.photo?.forEach { photo ->
                        val link =
                            "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
                        imagesList.add(Image(link, photo.title))
                    }
                }
            } catch (e: IOException) {
                Log.e(TOKEN_DOWNLOAD_JSON, "Failed download from $url")
            }
            return imagesList
        }

        override fun onPostExecute(result: List<Image>) {
            wr.get()?.afterDownload(result)
        }
    }

    internal fun afterDownload(result: List<Image>) {
        imagesList = result

        val viewManager = LinearLayoutManager(this@MainActivity)
        val imageAdapter = ImageAdapter(result) {
            val intent = Intent(this@MainActivity, ImageActivity::class.java)
            intent.putExtra("url", it.url)
            startActivity(intent)
        }

        rw.apply {
            adapter = imageAdapter
            layoutManager = viewManager
        }

        if (0 == imageAdapter.itemCount) {
            Toast.makeText(
                this,
                getString(R.string.no_results_found),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
