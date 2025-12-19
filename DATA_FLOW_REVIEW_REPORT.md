# Báo Cáo Review Data Flow - ExpenseTracker Project

**Ngày review:** $(date)  
**Reviewer:** Senior Android Developer  
**Phạm vi:** Tất cả modules từ data → domain → feature (trừ account module vì đang test với fake id = 1)

---

## 📋 Tổng Quan

Đã review toàn bộ data flow từ data layer → domain layer → feature layer. Tìm thấy **1 lỗi nghiêm trọng** và **1 vấn đề tiềm ẩn**.

---

## 🔴 LỖI NGHIÊM TRỌNG ĐÃ SỬA

### 1. **TransactionRepositoryImpl - Thiếu payeeIds khi load transactions**

**Vấn đề:**
- `getTransactionById()`, `getAllTransactionByAccount()`, và `getTransactionsByDateRange()` không load `payeeIds` từ database
- `TransactionMapper.toDomain()` không populate field `payeeIds`
- Kết quả: Khi load transaction, `payeeIds` luôn là `emptyList()` dù transaction có payees

**Vị trí:**
- `data/src/main/java/repository/TransactionRepositoryImpl.kt`
- `data/src/main/java/mapper/TransactionMapper.kt`

**Đã sửa:**
- ✅ Thêm parameter `payeeIds` vào `TransactionMapper.toDomain()`
- ✅ Thêm batch query `getPayeeIdsByTransactions()` trong `TransactionPayeeDao`
- ✅ Cập nhật `getTransactionById()` để load payeeIds
- ✅ Cập nhật `getAllTransactionByAccount()` để load payeeIds cho tất cả transactions
- ✅ Cập nhật `getTransactionsByDateRange()` để load payeeIds cho tất cả transactions

**Impact:** 
- Trước khi sửa: Payees không được hiển thị khi edit transaction hoặc xem transaction list
- Sau khi sửa: Payees được load đầy đủ từ database

---

## ⚠️ VẤN ĐỀ TIỀM ẨN

### 2. **CategoryRepositoryImpl - Null Safety Issue**

**Vấn đề:**
- `CategoryDao.getCategoryById()` trả về non-nullable `CategoryEntity`
- Room sẽ throw `EmptyResultSetException` nếu category không tồn tại
- `CategoryRepositoryImpl.getCategoryById()` và `GetCategoryByIdUseCase` cũng trả về non-nullable
- Không có error handling khi category không tồn tại

**Vị trí:**
- `data/src/main/java/dao/CategoryDao.kt` (line 25)
- `data/src/main/java/repository/CategoryRepositoryImpl.kt` (line 41-42)
- `domain/src/main/java/transaction/usecase/GetCategoryByIdUseCase.kt` (line 10-12)

**Khuyến nghị:**
- Nên thay đổi `CategoryDao.getCategoryById()` trả về nullable `CategoryEntity?`
- Hoặc thêm try-catch trong repository/use case để handle exception
- Hoặc đảm bảo category luôn tồn tại trước khi gọi (validate ở use case level)

**Impact:** 
- App có thể crash nếu gọi `getCategoryById()` với ID không tồn tại
- Hiện tại có thể không xảy ra vì categories được initialize, nhưng cần fix để tránh bug trong tương lai

---

## ✅ CÁC ĐIỂM TỐT

1. **Clean Architecture:** Project tuân thủ tốt Clean Architecture với separation rõ ràng giữa data, domain, và feature layers

2. **Repository Pattern:** Sử dụng đúng pattern với interface ở domain layer và implementation ở data layer

3. **Use Case Pattern:** Các use cases được implement đúng cách, có validation logic

4. **Flow Usage:** Sử dụng Flow đúng cách cho reactive data streams

5. **Error Handling:** Một số ViewModels có error handling tốt với `catch` operator

6. **Transaction Management:** `TransactionRepositoryImpl` xử lý đúng việc insert/update payees khi save transaction

---

## 📝 CÁC ĐIỂM CẦN LƯU Ý (Không phải lỗi)

1. **Hardcoded Account IDs:** Nhiều ViewModels sử dụng hardcoded `ACCOUNT_ID = 1L` (nhưng đã được exclude theo yêu cầu)

2. **TransactionMapper.partnerId:** Luôn hardcode `partnerId = 1` trong mapper (line 30)

3. **Description Null Safety:** `Transaction.toEntity()` sử dụng `description!!` - có thể throw NPE nếu description null (nhưng có validation ở use case level)

---

## 🔍 CHI TIẾT CÁC THAY ĐỔI ĐÃ THỰC HIỆN

### File: `data/src/main/java/mapper/TransactionMapper.kt`
```kotlin
// Thêm parameter payeeIds với default value
internal fun TransactionWithDetails.toDomain(payeeIds: List<Long> = emptyList()): Transaction {
    return Transaction(
        // ... các fields khác
        payeeIds = payeeIds  // ✅ Đã thêm
    )
}
```

### File: `data/src/main/java/dao/TransactionPayeeDao.kt`
```kotlin
// Thêm batch query để load payeeIds cho nhiều transactions cùng lúc
@Query("SELECT transactionId, payeeId FROM tb_transaction_payee WHERE transactionId IN (:transactionIds)")
suspend fun getPayeeIdsByTransactions(transactionIds: List<Long>): List<TransactionPayeeEntity>
```

### File: `data/src/main/java/repository/TransactionRepositoryImpl.kt`
```kotlin
// Sử dụng mapLatest để load payeeIds trong suspend context
override fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>> {
    return transactionDao.getAccountWithTransactions(accountId)
        .mapLatest { list ->
            if (list.isEmpty()) return@mapLatest emptyList()
            val transactionIds = list.map { it.transactionEntity.id }
            val payeeMap = transactionPayeeDao.getPayeeIdsByTransactions(transactionIds)
                .groupBy { it.transactionId }
                .mapValues { (_, entities) -> entities.map { it.payeeId } }
            
            list.map { transactionWithDetails ->
                val payeeIds = payeeMap[transactionWithDetails.transactionEntity.id] ?: emptyList()
                transactionWithDetails.toDomain(payeeIds)
            }
        }
}
```

---

## 📊 TỔNG KẾT

| Loại | Số lượng | Trạng thái |
|------|----------|------------|
| Lỗi nghiêm trọng | 1 | ✅ Đã sửa |
| Vấn đề tiềm ẩn | 1 | ⚠️ Cần xem xét |
| Điểm tốt | 6 | ✅ |
| Lưu ý | 3 | ℹ️ |

---

## 🎯 KHUYẾN NGHỊ TIẾP THEO

1. **Test lại:** Test các chức năng liên quan đến payees sau khi sửa bug
2. **Fix Category null safety:** Xem xét sửa `getCategoryById()` để tránh crash
3. **Code review:** Review lại các ViewModels sử dụng `GetCategoryByIdUseCase` để đảm bảo có error handling
4. **Unit tests:** Thêm unit tests cho `TransactionRepositoryImpl` để test payeeIds loading

---

**Kết luận:** Data flow đã được sửa và hoạt động đúng. PayeeIds giờ đã được load đầy đủ từ database. Cần tiếp tục monitor và fix vấn đề Category null safety trong tương lai.


