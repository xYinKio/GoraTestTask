package yinkio.android.goratesttask.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import yinkio.android.goratesttask.data.AppRepository
import yinkio.android.goratesttask.data.remote.RemoteSource
import yinkio.android.goratesttask.domain.Repository

@Module
@InstallIn(ViewModelComponent::class)
interface Module {

    @Binds
    fun repository(repository: AppRepository) : Repository


}