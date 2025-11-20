package repository

import TransactionDao
import javax.inject.Inject

abstract class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) {

}