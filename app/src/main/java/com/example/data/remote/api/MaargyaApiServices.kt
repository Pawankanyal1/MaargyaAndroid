package com.example.data.remote.api

import com.example.data.remote.ApiConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// DTOs
data class LoginRequestDto(val identity: String, val passwordHash: String)
data class AuthResponseDto(val token: String, val userId: String, val name: String, val message: String)
data class RegisterRequestDto(val fullName: String, val email: String, val mobile: String, val dob: String, val classLevel: String)
data class ForgotPasswordRequestDto(val emailOrMobile: String)
data class ResetPasswordRequestDto(val otp: String, val newPassword: String)

data class UserProfileDto(val id: String, val name: String, val email: String, val mobile: String, val classLevel: String, val targetCareer: String)

data class CareerLabProgressDto(val completedDays: Int, val currentDay: Int, val summary: Map<String, Any>?)
data class SubmitDayActivityDto(val dayNumber: Int, val answers: Map<String, String>)

data class HandwritingUploadResponseDto(val reportId: String, val status: String, val message: String)
data class HandwritingReportDto(val id: String, val writingSize: String, val slant: String, val spacing: String, val baseline: String, val pressure: String, val letterFormation: String, val summary: String)

data class GameResultSubmitDto(val gameType: String, val score: Int, val accuracy: Int, val timeSeconds: Int, val level: Int)
data class GameResultResponseDto(val status: String, val highscore: Int, val xpEarned: Int)

data class CareerSummaryDto(val id: String, val title: String, val category: String, val salaryRange: String, val shortDesc: String)
data class CourseSummaryDto(val id: String, val title: String, val instructor: String, val duration: String, val level: String)
data class NotificationDto(val id: String, val title: String, val message: String, val category: String, val timeAgo: String)

// Interfaces
interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<Map<String, String>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Map<String, String>>
}

interface UserApiService {
    @GET("users/me")
    suspend fun getCurrentUser(): Response<UserProfileDto>

    @PUT("users/me")
    suspend fun updateProfile(@Body profile: UserProfileDto): Response<UserProfileDto>
}

interface CareerLabApiService {
    @GET("career-lab")
    suspend fun getCareerLabDays(): Response<List<Map<String, Any>>>

    @GET("career-lab/progress")
    suspend fun getCareerLabProgress(): Response<CareerLabProgressDto>

    @POST("career-lab/activity")
    suspend fun submitActivity(@Body activity: SubmitDayActivityDto): Response<Map<String, Any>>
}

interface HandwritingApiService {
    @POST("handwriting/upload")
    suspend fun uploadSample(@Body metadata: Map<String, String>): Response<HandwritingUploadResponseDto>

    @GET("handwriting/report/{id}")
    suspend fun getReport(@Path("id") id: String): Response<HandwritingReportDto>
}

interface GamesApiService {
    @GET("games")
    suspend fun getGames(): Response<List<Map<String, Any>>>

    @POST("games/result")
    suspend fun submitGameResult(@Body result: GameResultSubmitDto): Response<GameResultResponseDto>
}

interface CareersApiService {
    @GET("careers")
    suspend fun getCareers(@Query("category") category: String? = null, @Query("query") query: String? = null): Response<List<CareerSummaryDto>>

    @GET("careers/{id}")
    suspend fun getCareerDetail(@Path("id") id: String): Response<Map<String, Any>>
}

interface CoursesApiService {
    @GET("courses")
    suspend fun getCourses(@Query("category") category: String? = null): Response<List<CourseSummaryDto>>

    @GET("courses/{id}")
    suspend fun getCourseDetail(@Path("id") id: String): Response<Map<String, Any>>

    @GET("courses/{id}/lessons")
    suspend fun getLessons(@Path("id") courseId: String): Response<List<Map<String, Any>>>

    @POST("progress")
    suspend fun updateProgress(@Body progress: Map<String, Any>): Response<Map<String, Any>>
}

interface NotificationsApiService {
    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationDto>>
}

object RetrofitClient {
    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    fun getRetrofit(baseUrl: String = ApiConfig.dynamicBaseUrl): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
