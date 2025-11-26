package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dao.AccountDao
import dao.CategoryDao
import dao.TransactionDao
import model.AccountEntity
import model.CategoryEntity
import model.TransactionEntity

@Database(
    entities = [TransactionEntity::class, AccountEntity::class, CategoryEntity::class],
    exportSchema = false,
    version = 1
)
internal abstract class BudgetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
}