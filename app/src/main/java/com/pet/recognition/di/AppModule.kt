package com.pet.recognition.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.pet.recognition.local.Database
import com.pet.recognition.local.dao.CurrentUserRepo
import com.pet.recognition.local.dao.CurrentUserRepoImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pet.recognition.ErrorNotificationService.ErrorNotifier
import com.pet.recognition.ErrorNotificationService.ErrorNotifierInterface
import com.pet.recognition.local.dao.Repository
import com.pet.recognition.remote.RemoteConsts
import com.pet.recognition.remote.api.ApiRepository
import com.pet.recognition.remote.api.ApiRepositoryImpl
import com.pet.recognition.remote.api.ApiRequestHelper
import com.pet.recognition.remote.api.PetConnectApi
import com.pet.recognition.remote.token.AuthAuthenticator
import com.pet.recognition.remote.token.AuthInterceptor
import com.pet.recognition.remote.token.TokenProvider
import com.pet.recognition.remote.token.TokenProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(app: Application): Database {
        return Room.databaseBuilder(
            app,
            Database::class.java,
            "recognition_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFaceRepo(app: Application): Repository = Repository(app)

    @Provides
    @Singleton
    fun provideCurrentUserRepository(db: Database): CurrentUserRepo {
        return CurrentUserRepoImpl(db.currentUserDao)
    }

    @Provides
    @Singleton
    fun provideApiRepository(api: PetConnectApi, apiRequestHelper: ApiRequestHelper): ApiRepository {
        return ApiRepositoryImpl(api,apiRequestHelper)
    }

    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenProvider = TokenProviderImpl(context)

    @Singleton
    @Provides
    fun provideApiResponseHelper(@ApplicationContext context: Context,tokenProvider: TokenProvider): ApiRequestHelper = ApiRequestHelper(tokenProvider,context)

    @Singleton
    @Provides
    fun provideErrorNotifier(@ApplicationContext context: Context) : ErrorNotifierInterface = ErrorNotifier(context)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS)).protocols(listOf( Protocol.HTTP_1_1))
            .authenticator(authAuthenticator)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenManager: TokenProvider): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Singleton
    @Provides
    fun provideAuthAuthenticator(tokenManager: TokenProvider): AuthAuthenticator =
        AuthAuthenticator(tokenManager)

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(RemoteConsts.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(MediaType.parse("application/json")!!))


    @Singleton
    @Provides
    fun provideMainAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): PetConnectApi =
        retrofit
            .client(okHttpClient)
            .build()
            .create(PetConnectApi::class.java)
}