package database

import androidx.room.Database
import androidx.room.RoomDatabase
import dao.AccountDao
import dao.CategoryDao
import dao.EventDao
import dao.TransactionDao
import model.AccountEntity
import model.CategoryEntity
import model.EventEntity
import model.PayeeEntity
import model.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        EventEntity::class,
        PayeeEntity::class
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