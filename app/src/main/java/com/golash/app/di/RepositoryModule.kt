package com.golash.app.di

import com.golash.app.data.repository.FirestoreProductRepository
import com.golash.app.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        firestoreProductRepository: FirestoreProductRepository
    ): ProductRepository
}