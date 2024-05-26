package com.example.tipjar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class FunctionalModule {
    @Provides
    fun provideCurrentTimeInMillis(): () -> Long = { System.currentTimeMillis() }
}