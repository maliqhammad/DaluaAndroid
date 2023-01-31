package com.dalua.app.di.modules

import com.dalua.app.api.ApiService
import com.dalua.app.api.RegistrationRepository
import com.dalua.app.api.RemoteDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    fun getRepository(
        apiService: ApiService,
        @Named("uniqueID") uniqueID: String
    ): RemoteDataRepository {
        return RemoteDataRepository(apiService, uniqueID)
    }

    @Provides
    fun getRegistrationModule(
        apiService: ApiService,
        @Named("uniqueID") uniqueID: String
    ): RegistrationRepository {
        return RegistrationRepository(apiService, uniqueID)
    }
}