package com.example.newsly

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.AdRequest
import com.littlemango.stacklayoutmanager.StackLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    private var mInterstitialAd: InterstitialAd? = null
    var pageNum = 1
    var totalResults = -1
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {
            load()
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.

                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    load()
                    mInterstitialAd=null
                }
            }
        }
        var newsList = findViewById<RecyclerView>(R.id.newsList)
        adapter = NewsAdapter(this@MainActivity, articles)
        newsList.adapter = adapter
        //newsList.layoutManager=LinearLayoutManager(this@MainActivity )
        val layoutManager = StackLayoutManager(StackLayoutManager.ScrollOrientation.BOTTOM_TO_TOP)
        layoutManager.setPagerMode(true)
        layoutManager.setPagerFlingVelocity(3000)

        layoutManager.setItemChangedListener(object : StackLayoutManager.ItemChangedListener {
            override fun onItemChanged(position: Int) {

                container.setBackgroundColor(Color.parseColor(ColorPicker.getColor()))
                Log.d(TAG, "First Visible Item - ${layoutManager.getFirstVisibleItemPosition()}")
                Log.d(TAG, "Total Count - ${layoutManager.itemCount}")
                if (totalResults > layoutManager.itemCount && layoutManager.getFirstVisibleItemPosition() >= layoutManager.itemCount - 5) {
                    //next page
                    pageNum++
                    getNews()
                }
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@MainActivity)
                }
            }

        })
        //set in recycler view
        newsList.layoutManager = layoutManager
        getNews()
    }
    fun load(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }


    //call singleton object
    private fun getNews() {
        Log.d(TAG, "Request sent for $pageNum")
        val news = NewsService.newsInstance.gateHeadlines(country = "in", pageNum)
        // requests are send in retrofit in a queue
        news.enqueue(object : retrofit2.Callback<News> {
            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("CHETNA", "Error in fetching code", t)
            }

            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news: News? = response.body()
                if (news != null) {
                    totalResults = news.totalResults
                    Log.d("CHETNA", news.toString())
                    articles.addAll(news.articles)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

}