package datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import dao.AccountDao
import dao.CategoryDao
import dao.EventDao
import dao.LocationDao
import dao.PayeeTransactionDao
import dao.TransactionDao
import dao.TransactionImageDao
import dao.TransactionPayeeDao
import model.AccountEntity
import model.CategoryEntity
import model.EventEntity
import model.LocationEntity
import model.PayeeEntity
import model.PayeeTransactionEntity
import model.TransactionEntity
import model.TransactionImageEntity
import model.TransactionPayeeEntity

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        EventEntity::class,
        PayeeEntity::class,
        PayeeTransactionEntity::class,
        LocationEntity::class,
        TransactionPayeeEntity::class,
        TransactionImageEntity::class
    ],
    exportSchema = false,
    version = 3
)
internal abstract class BudgetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun eventDao(): EventDao
    abstract fun payeeTransactionDao(): PayeeTransactionDao
    abstract fun locationDao(): LocationDao
    abstract fun transactionPayeeDao(): TransactionPayeeDao
    abstract fun transactionImageDao(): TransactionImageDao
}