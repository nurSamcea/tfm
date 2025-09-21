package com.example.frontend.di;

import com.example.frontend.api.ApiService;
import com.example.frontend.api.ApiClient;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import android.content.Context;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public ApiService provideApiService(@ApplicationContext Context context) {
        return ApiClient.getApiService(context);
    }
} 