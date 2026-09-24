package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.local.entities.UserProfileEntity
import com.example.data.remote.NetworkResult
import com.example.data.repository.AuthRepository
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val database: AppDatabase,
    private val userPrefs: UserPreferencesRepository
) : AuthRepository {

    override val currentUser: Flow<UserProfile?> = database.maargyaDao().getUserProfile()
        .map { entity ->
            entity?.let {
                UserProfile(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    mobile = it.mobile,
                    dob = it.dob,
                    classLevel = it.classLevel,
                    targetCareer = it.targetCareer,
                    isLoggedIn = it.isLoggedIn,
                    journeyProgress = it.journeyProgress
                )
            } ?: UserProfile()
        }

    override suspend fun login(identifier: String, password: String): NetworkResult<UserProfile> {
        if (identifier.isBlank() || password.isBlank()) {
            return NetworkResult.Error("Please provide both email/mobile and password")
        }
        val user = UserProfile(
            id = "user_default",
            name = if (identifier.contains("@")) identifier.substringBefore("@").replaceFirstChar { it.uppercase() } else "Pawan Kanyal",
            email = if (identifier.contains("@")) identifier else "pawankanyal1@gmail.com",
            mobile = if (!identifier.contains("@")) identifier else "+91 98765 43210",
            isLoggedIn = true,
            journeyProgress = 42
        )
        database.maargyaDao().insertOrUpdateUser(
            UserProfileEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                mobile = user.mobile,
                dob = user.dob,
                classLevel = user.classLevel,
                targetCareer = user.targetCareer,
                isLoggedIn = true,
                journeyProgress = user.journeyProgress
            )
        )
        userPrefs.setUserLoggedIn(true)
        return NetworkResult.Success(user)
    }

    override suspend fun register(
        fullName: String,
        email: String,
        mobile: String,
        dob: String,
        classLevel: String,
        password: String
    ): NetworkResult<UserProfile> {
        if (fullName.isBlank() || email.isBlank() || password.length < 6) {
            return NetworkResult.Error("Please complete all required fields (password minimum 6 characters)")
        }
        val user = UserProfile(
            id = "user_default",
            name = fullName,
            email = email,
            mobile = mobile,
            dob = dob,
            classLevel = classLevel,
            isLoggedIn = true,
            journeyProgress = 10
        )
        database.maargyaDao().insertOrUpdateUser(
            UserProfileEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                mobile = user.mobile,
                dob = user.dob,
                classLevel = user.classLevel,
                targetCareer = user.targetCareer,
                isLoggedIn = true,
                journeyProgress = 10
            )
        )
        userPrefs.setUserLoggedIn(true)
        return NetworkResult.Success(user)
    }

    override suspend fun forgotPassword(identifier: String): NetworkResult<String> {
        if (identifier.isBlank()) {
            return NetworkResult.Error("Please enter your registered email or mobile number")
        }
        return NetworkResult.Success("Verification code sent to $identifier")
    }

    override suspend fun verifyOtpAndReset(otp: String, newPass: String): NetworkResult<Boolean> {
        if (otp.length < 4 || newPass.length < 6) {
            return NetworkResult.Error("Enter valid 4-digit OTP and new password (min 6 chars)")
        }
        return NetworkResult.Success(true)
    }

    override suspend fun logout(): NetworkResult<Unit> {
        userPrefs.setUserLoggedIn(false)
        database.maargyaDao().insertOrUpdateUser(
            UserProfileEntity(
                id = "user_default",
                name = "Guest Learner",
                email = "",
                mobile = "",
                dob = "",
                classLevel = "Class 11-12",
                targetCareer = "Exploring Paths",
                isLoggedIn = false,
                journeyProgress = 0
            )
        )
        return NetworkResult.Success(Unit)
    }

    override suspend fun updateUserProfile(profile: UserProfile): NetworkResult<UserProfile> {
        database.maargyaDao().insertOrUpdateUser(
            UserProfileEntity(
                id = profile.id,
                name = profile.name,
                email = profile.email,
                mobile = profile.mobile,
                dob = profile.dob,
                classLevel = profile.classLevel,
                targetCareer = profile.targetCareer,
                isLoggedIn = profile.isLoggedIn,
                journeyProgress = profile.journeyProgress
            )
        )
        return NetworkResult.Success(profile)
    }
}
