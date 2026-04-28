package com.golash.app.di

import android.content.Context
import com.golash.app.data.db.AppDatabase
import com.golash.app.domain.repository.CartRepository
import com.golash.app.data.repository.cart.RoomCartRepository
import com.golash.app.data.repository.product.MockProductRepository
import com.golash.app.domain.repository.ProductRepository
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

    //TODO Change to ProductRepository in production - MockProductRepository for development
    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository = MockProductRepository()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)
}