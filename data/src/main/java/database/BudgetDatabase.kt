package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dao.AccountDao
import dao.TransactionDao
import model.AccountEntity
import model.CategoryEntity
import model.TransactionEntity

@Database(
    entities = [TransactionEntity::class, AccountEntity::class, CategoryEntity::class],
    exportSchema = true,
    version = 1
)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetDatabase? = null
        fun getDatabase(context: Context): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "budget_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}