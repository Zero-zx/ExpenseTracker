package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dao.AccountDao
import dao.CategoryDao
import dao.EventDao
import dao.TransactionDao
import model.AccountEntity
import model.CategoryEntity
import model.EventEntity
import model.EventParticipantEntity
import model.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        EventEntity::class,
        EventParticipantEntity::class
    ],
    exportSchema = false,
    version = 2
)
internal abstract class BudgetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun eventDao(): EventDao
}