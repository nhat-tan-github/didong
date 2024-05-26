package com.example.myapplication.model


import com.example.myapplication.model.ApiResponse
import com.example.myapplication.model.Exercise
import com.example.myapplication.model.MyClass
import com.example.myapplication.model.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
// đăng ký
    @POST("register")
    @FormUrlEncoded
    fun registerUser(@Field ("email") email : String,
                    @Field ("name") name: String,
                    @Field ("password") password: String) :Observable<String>
// đăng nhập
    @POST("login")
    @FormUrlEncoded
    fun loginUser(@Field ("email") email : String,
                  @Field ("password") password: String) :Observable<User>
// đổi mật khẩu
    @POST("/change-password")
    @FormUrlEncoded
    fun changePassword(
        @Field("email") email: String,
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String
    ): Call<ApiResponse>
// tạo bài tập/ bài đăng mới
    @POST("/post")
    @FormUrlEncoded
    fun createPost(
    @Field("class_id") classId: Int,
    @Field("author_id") authorId: Int,
    @Field("post_name") postName: String,
    @Field("post_content") postContent: String,
    @Field("link_drive") linkDrive: String,
    @Field("day_end") dayEnd: String
    ): Call<ApiResponse>
// edit lại 1 bài tập
@PUT("/edit-post/{postId}")
@FormUrlEncoded
fun editPost(
    @Path("postId") postId: String,
    @Field("author_id") authorId: Int,
    @Field("post_name") postName: String,
    @Field("post_content") postContent: String,
    @Field("link_drive") linkDrive: String,
    @Field("day_end") dayEnd: String
): Call<ApiResponse>

    // Tạo lớp mới
    @POST("create-class/")
    @FormUrlEncoded
    fun createClass(
        @Field("title") title: String,
        @Field("adminId") adminId: String
    ): Observable<ApiResponse>
// Tham gia lớp bằng ID lớp
    @POST("join-class/{classId}")
    @FormUrlEncoded
    fun joinClass(
        @Path("classId") classId: String,
        @Field("studentId") studentId: String
    ): Observable<String>

// Hiển thị bài trong 1 lớp
    @GET("post/{classId}")
    fun getPostsInClass(@Path("classId") classId: String): Call<List<Exercise>>

    // Hiển thị lớp đã tham gia
    @GET("class/user/{userId}")
    suspend fun getUserClasses(@Path("userId") userId: String): Response<List<MyClass>>

    // Hiển thị lớp đã tham gia
    @GET("/admin-classes/{adminId}")
    suspend fun getUserAdminClasses(@Path("adminId") adminId: String): Response<List<MyClass>>
// tìm bài bằng id
    @GET("/post-by-id/{postId}")
    fun getPostById(@Path("postId") postId: String): Call<Exercise>

// Nộp bài
    @POST("submit")
    @FormUrlEncoded
    fun submitAssignment(
        @Field("assignment_id") assignmentId: Int,
        @Field("student_id") studentId: Int,
        @Field("submission_content") submissionContent: String
    ): Observable<String>
// thoát lớp
    @POST("/leave-class/{classId}")
    @FormUrlEncoded
    fun outClass(
    @Path("classId") classId: String,
        @Field("studentId") studentId: String
    ): Observable<ApiResponse>
}
