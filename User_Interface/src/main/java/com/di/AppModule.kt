package com.di

import android.content.Context
import com.data.local.SharedPref
import com.permissions.Permissions
import com.permissions.PermissionsImpl
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
fun providePermissions(
@ApplicationContext context: Context
): Permissions = PermissionsImpl(context)


@Provides
fun provideSharedPreference(
@ApplicationContext context: Context
): SharedPref = SharedPref(context)


}


