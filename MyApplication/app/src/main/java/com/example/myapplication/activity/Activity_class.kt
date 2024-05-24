package com.example.myapplication.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.*
import com.example.myapplication.adapter.ExerciseAdapter
import com.example.myapplication.databinding.ActivityClassBinding
import com.example.myapplication.model.ApiClient
import com.example.myapplication.model.ApiService
import com.example.myapplication.model.Exercise
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_class : AppCompatActivity() {
    private lateinit var binding: ActivityClassBinding
    private lateinit var apiService: ApiService
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        if (!Utils.isOnline(this)) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(application.getString(R.string.app_name))
                .setMessage("Thiết bị của bạn không kết nối với Internet!")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        }

        val classTitle = intent.getStringExtra("classTitle")
        val classId = intent.getStringExtra("classId")
        binding.classTitle.text = classTitle
        val retrofit = ApiClient.instance
        apiService = retrofit.create(ApiService::class.java)
        // Hiển thị classId trong một Toast để kiểm tra

            fetchExercises(classId.toString())


        binding.back.setOnClickListener {
            finish()
        }


    }

    private fun fetchExercises(classId: String) {
        val call = apiService.getPostsInClass(classId)
        call.enqueue(object : Callback<List<Exercise>> {
            override fun onResponse(call: Call<List<Exercise>>, response: Response<List<Exercise>>) {
                if (response.isSuccessful) {
                    val exercises = response.body()
                    exercises?.let {
                        // Sử dụng ExerciseAdapter với dữ liệu mới
                        exerciseAdapter = ExerciseAdapter(this@activity_class, R.layout.list_exercise_layout, it)
                        binding.listClass.adapter = exerciseAdapter
                    }
                } else {
                    Log.e("MainActivity", "Lỗi khi hiển thị bài: ${response.message()}")
                    Toast.makeText(this@activity_class, "Hiện không có bài nào!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Exercise>>, t: Throwable) {
                Log.e("MainActivity", "Lỗi khi hiển thị bài: $t")
                Toast.makeText(this@activity_class, "Hiện không có bài nào!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
