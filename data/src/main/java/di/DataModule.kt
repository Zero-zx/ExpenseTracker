package di

import account.repository.AccountRepository
import android.content.Context
import androidx.room.Room
import contact.repository.PhoneContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dao.AccountDao
import dao.CategoryDao
import dao.EventDao
import dao.LocationDao
import dao.PayeeTransactionDao
import dao.TransactionDao
import dao.TransactionImageDao
import dao.TransactionPayeeDao
import dao.UserDao
import datasource.BudgetDatabase
import datasource.PhoneContactDataSource
import transaction.repository.CategoryRepository
import transaction.repository.EventRepository
import transaction.repository.LocationRepository
import transaction.repository.PayeeTransactionRepository
import transaction.repository.TransactionRepository
import repository.AccountRepositoryImpl
import repository.CategoryRepositoryImpl
import repository.EventRepositoryImpl
import repository.LocationRepositoryImpl
import repository.PayeeTransactionRepositoryImpl
import repository.PhoneContactRepositoryImpl
import repository.SessionRepositoryImpl
import repository.TransactionRepositoryImpl
import repository.UserRepositoryImpl
import session.UserSessionManager
import session.repository.SessionRepository
import user.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): BudgetDatabase {
        return Room.databaseBuilder(
            context,
            BudgetDatabase::class.java,
            "budget_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: BudgetDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: BudgetDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideAccountDao(appDatabase: BudgetDatabase): AccountDao {
        return appDatabase.accountDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(appDatabase: BudgetDatabase): EventDao {
        return appDatabase.eventDao()
    }

    @Provides
    @Singleton
    fun providePayeeTransactionDao(appDatabase: BudgetDatabase): PayeeTransactionDao {
        return appDatabase.payeeTransactionDao()
    }

    @Provides
    @Singleton
    fun provideLocationDao(appDatabase: BudgetDatabase): LocationDao {
        return appDatabase.locationDao()
    }

    @Provides
    @Singleton
    fun provideTransactionPayeeDao(appDatabase: BudgetDatabase): TransactionPayeeDao {
        return appDatabase.transactionPayeeDao()
    }

    @Provides
    @Singleton
    fun provideTransactionImageDao(appDatabase: BudgetDatabase): TransactionImageDao {
        return appDatabase.transactionImageDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: BudgetDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideUserSessionManager(@ApplicationContext context: Context): UserSessionManager {
        return UserSessionManager(context)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        transactionPayeeDao: TransactionPayeeDao
    ): TransactionRepository {
        return TransactionRepositoryImpl(transactionDao, transactionPayeeDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }

    @Provides
    @Singleton
    fun provideAccountRepository(accountDao: AccountDao): AccountRepository {
        return AccountRepositoryImpl(accountDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(sessionManager: UserSessionManager): SessionRepository {
        return SessionRepositoryImpl(sessionManager)
    }

    @Provides
    @Singleton
    fun provideEventRepository(eventDao: EventDao): EventRepository {
        return EventRepositoryImpl(eventDao)
    }

    @Provides
    @Singleton
    fun providePayeeTransactionRepository(payeeTransactionDao: PayeeTransactionDao): PayeeTransactionRepository {
        return PayeeTransactionRepositoryImpl(payeeTransactionDao)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(locationDao: LocationDao): LocationRepository {
        return LocationRepositoryImpl(locationDao)
    }

    @Provides
    @Singleton
    fun providePhoneContactDataSource(@ApplicationContext context: Context): PhoneContactDataSource {
        return PhoneContactDataSource(context)
    }

    @Provides
    @Singleton
    fun providePhoneContactRepository(phoneContactDataSource: PhoneContactDataSource): PhoneContactRepository {
        return PhoneContactRepositoryImpl(phoneContactDataSource)
    }

    @Provides
    @Singleton
    fun provideFileProvider(fileManager: datasource.storage.FileManager): storage.FileProvider {
        return fileManager
    }

    @Provides
    @Singleton
    fun provideTransactionImageRepository(
        fileManager: datasource.storage.FileManager,
        imageDao: TransactionImageDao
    ): transaction.repository.TransactionImageRepository {
        return repository.TransactionImageRepositoryImpl(fileManager, imageDao)
    }
}