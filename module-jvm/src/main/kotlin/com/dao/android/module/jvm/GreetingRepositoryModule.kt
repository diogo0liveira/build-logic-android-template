package com.dao.android.module.jvm

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface GreetingRepositoryModule {
    @Binds
    @Singleton
    fun providesGreetingRepository(impl: DefaultGreetingRepository): GreetingRepository
}
