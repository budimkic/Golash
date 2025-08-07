package com.golash.app.di

import android.content.Context
import com.golash.app.data.db.AppDatabase
import com.golash.app.data.repository.cart.CartRepository
import com.golash.app.data.repository.cart.RoomCartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCartRepository(db: AppDatabase): CartRepository = RoomCartRepository(db)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)
}