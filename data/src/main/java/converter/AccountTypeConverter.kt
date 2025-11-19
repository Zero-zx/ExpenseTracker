class AccountTypeConverter {
    @TypeConverter
    fun toAccountType(value: String): AccountType {
        return AccountType.from(value)
    }

    @TypeConverter
    fun fromAccountType(accountType: AccountType): String {
        return accountType.rawValue
    }
}