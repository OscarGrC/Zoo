package oscargrc.zoo.di

import oscargrc.zoo.config.AppConfig
import oscargrc.zoo.repositories.AnimalesRepository
import oscargrc.zoo.repositories.AnimalesRepositoryImpl
import oscargrc.zoo.services.database.SqlDeLightClient
import oscargrc.zoo.services.storage.StorageAnimales
import oscargrc.zoo.services.storage.StorageAnimalesImpl
import oscargrc.zoo.viewmodels.MainViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/***
 * El modulo que usa Koin para saber que inyectar en cada sitio. importante bind de clases dependientes
 */
val AppDIModule = module {
    singleOf(::AppConfig)
    singleOf(::SqlDeLightClient)
    singleOf(::AnimalesRepositoryImpl) {
        bind<AnimalesRepository>()
    }
    singleOf(::StorageAnimalesImpl) {
        bind<StorageAnimales>()
    }
    singleOf(::MainViewModel)
}