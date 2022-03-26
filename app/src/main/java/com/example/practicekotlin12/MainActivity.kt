package com.example.practicekotlin12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.practicekotlin12.adapter.BookAdapter
import com.example.practicekotlin12.adapter.HistoryAdapter
import com.example.practicekotlin12.api.BookService
import com.example.practicekotlin12.databinding.ActivityMainBinding
import com.example.practicekotlin12.model.History
import com.example.practicekotlin12.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // layout의 이름이 activity_main이기 때문에 해당 binding의 이름 또한 ActivityMainBinding이다.
        // activity 내부에는 layoutInflater가 있기 때문에 따로 불러오지 않고 바로 인자를 넣을 수 있다
        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()

        db = getAppDatabase(this)

        val retrofit = Retrofit.Builder() // retrofit 설정
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java) // .create 함수를 이용하여 BookService 구현


    }

    private fun search(text: String) {
        bookService.getBooksByName(getString(R.string.naverApiKey), getString(R.string.naverApiPassword), text)
            .enqueue(object : Callback<SearchBookDto> {
                override fun onResponse( // 응답에 성공했을 때 해당 메서드로 응답
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {

                    hideHistoryView()
                    saveSearchKeyword(text)

                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "Not Success")
                        return
                    }

                    response.body()?.let { // json 파일의 body 부분
                        Log.d(TAG, it.toString())

//                        it.books.forEach { book ->  // 받아온 책 정보 각각 Log에 띄움
//                            Log.d(TAG, it.toString())
//                        }

                        adapter.submitList(it.books) // 리스트를 인자의 것으로 바꿈
                    }
                }

                override fun onFailure(
                    call: Call<SearchBookDto>,
                    t: Throwable
                ) { // 응답에 실패했을 때 해당 메서드로 응답
                    hideHistoryView()

                    Log.e(TAG, t.toString())
                }

            })
    }

    private fun initBookRecyclerView() {
        adapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it) // Book 클래스를 Parcelable 로 반환 값을 줬기 때문에 인텐트로 한번에 넘길 수 있게 됨
            startActivity(intent)
        })

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this) // layout이 실제로 어떻게 그려질지에 대한 것을 나타냄
        binding.bookRecyclerView.adapter = adapter
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
        initSearchEditTextView()
    }

    private fun initSearchEditTextView() {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {// 엔터를 눌렀을 때 &&
                /*
              ACTION_DOWN : 눌렀을 때,
              ACTION_UP : 뗐을 때
              두 이벤트 모두 간격을 간격을 확인할 때 사용 (간격을 통해 LongPress 인지에 대해 판별)
             */
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true // 이벤트 처리했음(setOnKeyListener 는 반환값을 가짐)
            }
            return@setOnKeyListener false // 이벤트 처리안됨
        }

        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun showHistoryView() { // 검색 기록 보여주기
        Thread {
            val keywords = db.historyDao().getAll().reversed()

            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()

        binding.historyRecyclerView.isVisible = true
    }

    private fun hideHistoryView() { // 검색 기록 숨기기
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) { // 검색 기록 저장
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://openapi.naver.com/"
    }
}

/*
RecyclerView 구현을 위해서,
LayoutManager와 Adapter가 필요함
 */