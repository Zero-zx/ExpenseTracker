# Báo Cáo Review Business Logic - ExpenseTracker Project

**Ngày review:** $(date)  
**Reviewer:** Senior Android Developer  
**Phạm vi:** Tất cả business logic trong use cases và ViewModels

---

## 📋 Tổng Quan

Đã review toàn bộ business logic trong project. Tìm thấy **3 vấn đề về tính nhất quán** và **1 vấn đề về logic** cần xem xét.

---

## ⚠️ VẤN ĐỀ VỀ TÍNH NHẤT QUÁN

### 1. **IncomeAnalysisViewModel - Thiếu LEND trong filter**

**Vấn đề:**
- `IncomeAnalysisViewModel` chỉ filter `CategoryType.INCOME`
- Nhưng `ReportsViewModel` và `TransactionListViewModel` thì tính cả `INCOME` và `LEND` là income
- Kết quả: Income Analysis không bao gồm các giao dịch LEND, dẫn đến số liệu không khớp với Reports

**Vị trí:**
- `feature/statistics/src/main/java/presentation/detail/IncomeAnalysisViewModel.kt` (line 75)

**Code hiện tại:**
```kotlin
val isIncome = transaction.category.type == CategoryType.INCOME
```

**Khuyến nghị:**
```kotlin
val isIncome = transaction.category.type == CategoryType.INCOME || 
               transaction.category.type == CategoryType.LEND
```

**Impact:** 
- Income Analysis sẽ hiển thị số liệu thấp hơn thực tế vì không bao gồm LEND transactions

---

### 2. **ExpenseAnalysisViewModel - Thiếu BORROWING trong filter**

**Vấn đề:**
- `ExpenseAnalysisViewModel` chỉ filter `CategoryType.EXPENSE`
- Nhưng `ReportsViewModel` và `TransactionListViewModel` thì tính cả `EXPENSE` và `BORROWING` là expense
- Kết quả: Expense Analysis không bao gồm các giao dịch BORROWING, dẫn đến số liệu không khớp với Reports

**Vị trí:**
- `feature/statistics/src/main/java/presentation/detail/ExpenseAnalysisViewModel.kt` (line 75)

**Code hiện tại:**
```kotlin
val isExpense = transaction.category.type == CategoryType.EXPENSE
```

**Khuyến nghị:**
```kotlin
val isExpense = transaction.category.type == CategoryType.EXPENSE || 
                transaction.category.type == CategoryType.BORROWING
```

**Impact:** 
- Expense Analysis sẽ hiển thị số liệu thấp hơn thực tế vì không bao gồm BORROWING transactions

---

### 3. **UpdateTransactionUseCase vs AddTransactionUseCase - Validation không nhất quán**

**Vấn đề:**
- `UpdateTransactionUseCase` yêu cầu `description` không được blank (line 38)
- `AddTransactionUseCase` thì không có validation này
- Kết quả: Có thể add transaction với description null/blank, nhưng không thể update

**Vị trí:**
- `feature/transaction/src/main/java/usecase/AddTransactionUseCase.kt` (line 35)
- `feature/transaction/src/main/java/usecase/UpdateTransactionUseCase.kt` (line 38)

**Code hiện tại:**
```kotlin
// AddTransactionUseCase - không có validation description
require(transaction.amount > 0) { "Amount must be greater than 0" }

// UpdateTransactionUseCase - có validation description
require(transaction.amount > 0) { "Amount must be greater than 0" }
require(transaction.description?.isNotBlank() == true) { "Description cannot be blank" }
```

**Khuyến nghị:**
- Nếu description là required: Thêm validation vào `AddTransactionUseCase`
- Nếu description là optional: Xóa validation khỏi `UpdateTransactionUseCase`
- Hoặc thống nhất: Cả hai đều cho phép null/blank hoặc cả hai đều require

**Impact:** 
- User có thể tạo transaction không có description nhưng không thể update nó sau đó
- Hoặc ngược lại, có thể update transaction thành không có description nhưng không thể tạo mới

---

## 🔍 VẤN ĐỀ VỀ LOGIC

### 4. **AddTransactionViewModel - Event participants không đúng**

**Vấn đề:**
- Khi persist temporary event, luôn dùng `listOf("Me")` làm participants (line 154)
- Không sử dụng participants thực tế từ event (nếu có)
- Event có thể có `numberOfParticipants` > 1 nhưng chỉ lưu 1 participant "Me"

**Vị trí:**
- `feature/transaction/src/main/java/presentation/add/viewModel/AddTransactionViewModel.kt` (line 148-155)

**Code hiện tại:**
```kotlin
if (selectedEvent != null) {
    val eventId = addEventUseCase(
        eventName = selectedEvent.eventName,
        startDate = selectedEvent.startDate,
        endDate = selectedEvent.endDate,
        numberOfParticipants = selectedEvent.numberOfParticipants,
        accountId = selectedEvent.accountId,
        participants = listOf("Me") // Default participant - KHÔNG ĐÚNG!
    )
    finalEvent = selectedEvent.copy(id = eventId)
}
```

**Khuyến nghị:**
- Nếu event có participants thực tế: Sử dụng participants từ event
- Nếu event không có participants: Tạo participants dựa trên `numberOfParticipants`
- Hoặc: Không persist event nếu nó là temporary và chỉ lưu eventId reference

**Impact:** 
- Event được tạo với số participants không đúng
- Có thể gây confusion khi xem event details sau này

---

## ✅ CÁC ĐIỂM TỐT

1. **Validation Logic:** Các use cases có validation tốt với `require()` statements
   - ✅ `AddPayeeUseCase`: Validate name không blank
   - ✅ `AddLocationUseCase`: Validate name không blank
   - ✅ `AddEventUseCase`: Validate eventName, numberOfParticipants, date range
   - ✅ `AddTransactionUseCase`: Validate amount > 0

2. **Duplicate Prevention:** 
   - ✅ `AddPayeeUseCase`: Check existing payee trước khi insert
   - ✅ `AddLocationUseCase`: Check existing location trước khi insert

3. **Date Validation:**
   - ✅ `AddEventUseCase`: Validate endDate >= startDate

4. **Transaction Calculation:**
   - ✅ `TransactionListViewModel`: Tính toán đúng với sign (EXPENSE/BORROWING là negative, INCOME/LEND là positive)
   - ✅ `ReportsViewModel`: Tính toán đúng với INCOME/LEND và EXPENSE/BORROWING

5. **Temporary Data Handling:**
   - ✅ `AddTransactionViewModel`: Xử lý tốt temporary events, payees, locations với negative IDs

---

## 📊 TỔNG KẾT

| Loại | Số lượng | Mức độ | Trạng thái |
|------|----------|--------|------------|
| Vấn đề nhất quán | 3 | ⚠️ Medium | Cần sửa |
| Vấn đề logic | 1 | ⚠️ Medium | Cần xem xét |
| Điểm tốt | 5 | ✅ | OK |

---

## 🎯 KHUYẾN NGHỊ

### Ưu tiên cao:
1. **Sửa IncomeAnalysisViewModel:** Thêm LEND vào filter để nhất quán với ReportsViewModel
2. **Sửa ExpenseAnalysisViewModel:** Thêm BORROWING vào filter để nhất quán với ReportsViewModel

### Ưu tiên trung bình:
3. **Thống nhất validation:** Quyết định description là required hay optional và áp dụng nhất quán
4. **Sửa Event participants:** Xử lý đúng participants khi persist temporary event

---

## 💡 GỢI Ý CẢI THIỆN

1. **Tạo Constants cho Category Types:**
   ```kotlin
   object CategoryTypeGroups {
       val INCOME_TYPES = listOf(CategoryType.INCOME, CategoryType.LEND)
       val EXPENSE_TYPES = listOf(CategoryType.EXPENSE, CategoryType.BORROWING)
   }
   ```
   Sử dụng constants này ở tất cả nơi để đảm bảo nhất quán

2. **Tạo Use Case cho Transaction Validation:**
   - Tách validation logic ra một use case riêng
   - Sử dụng lại ở cả Add và Update

3. **Event Participants Logic:**
   - Nếu event có participants từ UI: Sử dụng participants đó
   - Nếu không: Tạo default participants dựa trên numberOfParticipants

---

**Kết luận:** Business logic cơ bản đã OK, nhưng có một số vấn đề về tính nhất quán cần được sửa để đảm bảo số liệu chính xác và trải nghiệm người dùng tốt hơn.


