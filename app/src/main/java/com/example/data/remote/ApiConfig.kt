package com.example.data.remote

import com.example.BuildConfig

object ApiConfig {
    const val DEFAULT_BASE_URL: String = BuildConfig.MAARGYA_API_URL

    var dynamicBaseUrl: String = DEFAULT_BASE_URL
        private set

    fun updateBaseUrl(newUrl: String) {
        dynamicBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
    }
}

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T, val statusCode: Int = 200) : NetworkResult<T>()
    data class Error(val message: String, val statusCode: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()

    companion object {
        fun getFriendlyErrorMessage(statusCode: Int?, defaultMessage: String? = null): String {
            return when (statusCode) {
                400 -> "Invalid request. Please check your inputs and try again."
                401 -> "Session expired. Please log in again to continue."
                403 -> "Access restricted. You do not have permission for this resource."
                404 -> "The requested content could not be found."
                409 -> "An account or entry with these details already exists."
                422 -> "Validation failed. Please verify the entered information."
                429 -> "Too many requests. Please wait a moment before retrying."
                500 -> "Server is momentarily unavailable. Working offline."
                502, 503, 504 -> "Service temporarily unreachable. Using local offline cache."
                else -> defaultMessage ?: "Something went wrong. Showing offline data."
            }
        }
    }
}
