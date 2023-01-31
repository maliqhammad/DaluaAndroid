package com.dalua.app.di.modules

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.dalua.app.api.ApiService
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Named("BASEURL")
    fun getBaseUrl(): String {
        return AppConstants.BASE_URL
    }

    @SuppressLint("HardwareIds")
    @Provides
    @Named("uniqueID")
    fun getUniqueId(@ApplicationContext context: Context): String {
        val k = Settings.Secure.getString(
            (context as Context).contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.d("UNIQUE_ID", "getUniqueId: $k")
        return k
    }

    @Singleton
    @Provides
    fun getApiService(
        @Named("BASEURL") baseurl: String,
        converterFactory: GsonConverterFactory,
        client: OkHttpClient
    ): ApiService {

        return Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
            .create(ApiService::class.java)

    }

    @Provides
    fun getClient(@ApplicationContext context: Context): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor)
            .addInterceptor {
                val requestBuilder = it.request().newBuilder()

                val string = ProjectUtil.getApiTokenBearer(context)
                string?.apply {
                    requestBuilder.addHeader("Authorization", this)
                }
                it.proceed(requestBuilder.build())
            }
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).build()

    }

    @Provides
    fun getConvertorFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return GsonConverterFactory.create(gson)
    }

    fun getInstance(baseurl: String): Retrofit {
        var mHttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        var mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(mHttpLoggingInterceptor)
            .build()


        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
        return retrofit
    }

//    @Singleton
//    @Provides
//    fun getDatabase(application: Application): RestaurentDatabase =
//        Room.databaseBuilder(application, RestaurentDatabase::class.java, "restaurent_database")
//            .build()


}