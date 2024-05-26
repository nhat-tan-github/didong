package com.example.myapplication.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.databinding.ActivitySubmitBinding
import com.example.myapplication.model.ApiClient
import com.example.myapplication.model.ApiService
import com.example.myapplication.model.Exercise
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySubmit : AppCompatActivity() {
    private lateinit var binding: ActivitySubmitBinding
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        // Kiểm tra kết nối internet
        if (!Utils.isOnline(this)) {
            showErrorDialog("Không có kết nối Internet. Vui lòng kiểm tra lại.")
            return
        }
        apiService = ApiClient.instance.create(ApiService::class.java)

        // Bắt đầu gửi yêu cầu API
        val postTitle = intent.getStringExtra("postTitle")
        val postId = intent.getStringExtra("postID")

        binding.exName.text = postTitle

        fetchPostData(postId.toString())

        binding.back.setOnClickListener {
            finish()
        }
        binding.exlinkdrive.setTextIsSelectable(true)
    }

    private fun fetchPostData(postId: String) {
        val call = apiService.getPostById(postId)
        call.enqueue(object : Callback<Exercise> {
            override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        binding.dayEnd.text = result.dayEnd
                        binding.exName.text = result.postName
                        binding.excontext.text = result.postContent
                        binding.exlinkdrive.text = result.link_drive

                    } else {
                        showErrorDialog("Dữ liệu không tìm thấy. Vui lòng thử lại sau.")
                    }
                } else {
                    showErrorDialog("Lỗi máy chủ: ${response.code()}. Vui lòng thử lại sau.")
                }
            }

            override fun onFailure(call: Call<Exercise>, t: Throwable) {
                showErrorDialog("Lỗi kết nối: ${t.localizedMessage}. Vui lòng thử lại.")
            }
        })
    }

    private fun showErrorDialog(message: String) {
        Handler(Looper.getMainLooper()).post {
            AlertDialog.Builder(this)
                .setTitle(application.getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton("Thử lại") { dialog, _ ->
                    dialog.dismiss()
                    fetchPostData(intent.getStringExtra("postID").toString())
                }
                .setNegativeButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        }
    }
}
