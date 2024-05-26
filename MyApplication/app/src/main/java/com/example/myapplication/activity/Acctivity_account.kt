package com.example.myapplication.activity

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.AcctivityAccountBinding
import com.example.myapplication.model.ApiClient
import com.example.myapplication.model.ApiResponse
import com.example.myapplication.model.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class acctivity_account : AppCompatActivity() {
    private lateinit var binding: AcctivityAccountBinding
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPreferences
    private val changePasswordDialog: Dialog by lazy {
        Dialog(this, R.style.CustomDialogTheme).apply {
            setContentView(R.layout.change_password_layout)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcctivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        binding.logout.setOnClickListener {
            clearSession()
            finishAffinity()
            System.exit(0)
        }
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        val retrofit = ApiClient.instance
        apiService = retrofit.create(ApiService::class.java)

        binding.changePassword.setOnClickListener {
            val email = changePasswordDialog.findViewById<EditText>(R.id.email)
            val oldPassword = changePasswordDialog.findViewById<EditText>(R.id.oldPassword)
            val newPassword = changePasswordDialog.findViewById<EditText>(R.id.newPassword)
            val btnUpdate = changePasswordDialog.findViewById<Button>(R.id.btnUpdate)
            val useremail = sharedPref.getString("email", null)
            email.setText(useremail)

            btnUpdate.setOnClickListener {
                val inputOldPassword = oldPassword.text.toString()
                val inputNewPassword = newPassword.text.toString()

                if (inputOldPassword.isEmpty() || inputNewPassword.isEmpty()) {
                    Toast.makeText(this@acctivity_account, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val call = apiService.changePassword(email.text.toString(), inputOldPassword, inputNewPassword)
                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        runOnUiThread {
                            if (response.isSuccessful || response.body()?.message?.contains("Password updated successfully") == true) {
                                Toast.makeText(this@acctivity_account, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                changePasswordDialog.dismiss()
                            } else {
                                Toast.makeText(this@acctivity_account, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                changePasswordDialog.dismiss()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        runOnUiThread {
                            Toast.makeText(this@acctivity_account, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                            changePasswordDialog.dismiss()
                        }
                    }
                })
            }
            val close = changePasswordDialog.findViewById<ImageView>(R.id.CloseImg)
            close.setOnClickListener{
                changePasswordDialog.dismiss()
            }
            changePasswordDialog.show()
        }
    }

    private fun clearSession() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
