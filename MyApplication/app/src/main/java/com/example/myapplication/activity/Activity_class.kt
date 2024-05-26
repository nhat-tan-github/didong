package com.example.myapplication.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.*
import com.example.myapplication.adapter.ExerciseAdapter
import com.example.myapplication.databinding.ActivityClassBinding
import com.example.myapplication.model.ApiClient
import com.example.myapplication.model.ApiResponse
import com.example.myapplication.model.ApiService
import com.example.myapplication.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class activity_class : AppCompatActivity() {
    private lateinit var binding: ActivityClassBinding
    private lateinit var apiService: ApiService
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var sharedPref: SharedPreferences
    private var selectedDate: String = ""
    private lateinit var dayEndEditText: EditText

    private val createExerciseDialog: Dialog by lazy {
        Dialog(this, R.style.CustomDialogTheme).apply {
            setContentView(R.layout.create_exercise_dialog)
        }
    }
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

        fetchExercises(classId.toString())

        binding.createExercises.setOnClickListener {
            val exTitle = createExerciseDialog.findViewById<EditText>(R.id.exTitle)
            val exContext = createExerciseDialog.findViewById<EditText>(R.id.exContext)
            dayEndEditText = createExerciseDialog.findViewById(R.id.dayEnd)
            val exLink= createExerciseDialog.findViewById<EditText>(R.id.exLink)
            val create = createExerciseDialog.findViewById<Button>(R.id.btnCreateex)

            // Gán sự kiện click cho trường day_end để mở DatePickerDialog
            dayEndEditText.setOnClickListener{
                showDatePickerDialog()
            }

            create.setOnClickListener {
                val inpexTitle = exTitle.text.toString()
                val inpexContext = exContext.text.toString()
                val inpexLink = exLink.text.toString()
                val inpday_end = selectedDate // Sử dụng ngày đã chọn từ DatePickerDialog
                val userId = sharedPref.getInt("id", -1).toString()

                if (!classId.isNullOrEmpty() && !userId.isNullOrEmpty() && classId != "-1") {
                    val call = apiService.createPost(
                        classId.toInt(),
                        userId.toInt(),
                        inpexTitle,
                        inpexContext,
                        inpexLink,
                        inpday_end
                    )
                    call.enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@activity_class,
                                    "Bài tập đã được tạo thành công!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Tải lại danh sách bài tập sau khi đã tạo thành công
                                createExerciseDialog.dismiss()
                                fetchExercises(classId.toString())
                            } else {
                                val responseBody = response.errorBody()?.string()
                                if (responseBody?.contains("You are not authorized to create a post for this class") == true) {
                                    Toast.makeText(this@activity_class, "Bạn không phải admin!", Toast.LENGTH_SHORT)
                                        .show()
                                    createExerciseDialog.dismiss()
                                } else {
                                    Log.e("activity_class", "Phản hồi thất bại: ${response.message()}")
                                    Toast.makeText(this@activity_class, "Phản hồi thất bại!", Toast.LENGTH_SHORT)
                                        .show()
                                    createExerciseDialog.dismiss()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Log.e("activity_class", "Lỗi khi tạo bài tập: $t")
                            Toast.makeText(this@activity_class, "Lỗi khi tạo bài tập!", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@activity_class, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            val close = createExerciseDialog.findViewById<ImageView>(R.id.closeexCreate)
            close.setOnClickListener {
                createExerciseDialog.dismiss()
            }
            createExerciseDialog.show()
        }
        binding.back.setOnClickListener {
            finish()
        }
        // chức năng edit exercise/ click vào 1 bài tập
        binding.listExercise.setOnItemClickListener { _, view, position, _ ->
            val selectedClass = exerciseAdapter.getItem(position)
            val intent = Intent(this@activity_class, ActivitySubmit::class.java)
            intent.putExtra("postID", selectedClass?.postId.toString())
            intent.putExtra("postTitle", selectedClass?.postName)
            startActivity(intent)
        }
    }



    // Hàm để hiển thị DatePickerDialog
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Format ngày được chọn vào định dạng YYYY-MM-DD
                selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                // Hiển thị ngày đã chọn trên EditText
                dayEndEditText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    private fun fetchExercises(classId: String) {
        val call = apiService.getPostsInClass(classId)
        call.enqueue(object : Callback<List<Exercise>> {
            override fun onResponse(call: Call<List<Exercise>>, response: Response<List<Exercise>>) {
                if (response.isSuccessful) {
                    val exercises = response.body()
                    exercises?.let {
                        // Sử dụng ExerciseAdapter với dữ liệu mới
                        exerciseAdapter =
                            ExerciseAdapter(this@activity_class, R.layout.list_exercise_layout, it)
                        binding.listExercise.adapter = exerciseAdapter
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
