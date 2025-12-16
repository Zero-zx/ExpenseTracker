package datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import converter.StringListConverter
import dao.AccountDao
import dao.CategoryDao
import dao.EventDao
import dao.LocationDao
import dao.PayeeTransactionDao
import dao.TransactionDao
import dao.TransactionImageDao
import dao.TransactionPayeeDao
import dao.UserDao
import model.AccountEntity
import model.CategoryEntity
import model.EventEntity
import model.LocationEntity
import model.PayeeEntity
import model.TransactionEntity
import model.TransactionImageEntity
import model.TransactionPayeeEntity
import model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TransactionEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        EventEntity::class,
        PayeeEntity::class,
        LocationEntity::class,
        TransactionPayeeEntity::class,
        TransactionImageEntity::class
    ],
    exportSchema = false,
    version = 5
)
@TypeConverters(StringListConverter::class)
internal abstract class BudgetDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun eventDao(): EventDao
    abstract fun payeeTransactionDao(): PayeeTransactionDao
    abstract fun locationDao(): LocationDao
    abstract fun transactionPayeeDao(): TransactionPayeeDao
    abstract fun transactionImageDao(): TransactionImageDao
}