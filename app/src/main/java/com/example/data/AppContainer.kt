package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.remote.NetworkMonitor
import com.example.data.repository.*
import com.example.data.repository.impl.*

interface AppContainer {
    val database: AppDatabase
    val userPreferences: UserPreferencesRepository
    val networkMonitor: NetworkMonitor
    val authRepository: AuthRepository
    val careerLabRepository: CareerLabRepository
    val handwritingRepository: HandwritingRepository
    val gamesRepository: GamesRepository
    val careerRepository: CareerRepository
    val courseRepository: CourseRepository
    val notificationRepository: NotificationRepository
}

class AppContainerImpl(context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val userPreferences: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database, userPreferences)
    }

    override val careerLabRepository: CareerLabRepository by lazy {
        CareerLabRepositoryImpl(database)
    }

    override val handwritingRepository: HandwritingRepository by lazy {
        HandwritingRepositoryImpl(database)
    }

    override val gamesRepository: GamesRepository by lazy {
        GamesRepositoryImpl(database)
    }

    override val careerRepository: CareerRepository by lazy {
        CareerRepositoryImpl(database)
    }

    override val courseRepository: CourseRepository by lazy {
        CourseRepositoryImpl(database)
    }

    override val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(database)
    }
}

typealias DefaultAppContainer = AppContainerImpl
