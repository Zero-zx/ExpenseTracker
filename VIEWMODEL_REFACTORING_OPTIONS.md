# 📋 Các Cách Giảm Complexity AddTransactionViewModel - Senior Android Dev Approach

## 🎯 Tổng quan

File hiện tại: **490 dòng**, quá nhiều responsibilities. Dưới đây là các cách **đơn giản và thiết
thực** mà senior Android dev sẽ làm, từ dễ đến khó.

---

## ✅ **CÁCH 1: Extract Persistence Logic vào UseCase** (Đơn giản nhất, nên làm đầu tiên)

### Vấn đề:

- Logic persist temporary data (event, payee, location) nằm trong `addTransaction()` - quá dài (90+
  dòng)
- Khó test riêng phần persistence

### Giải pháp:

Tạo `PrepareTransactionDataUseCase` để handle tất cả logic persist temporary data.

```kotlin
// domain/transaction/usecase/PrepareTransactionDataUseCase.kt
class PrepareTransactionDataUseCase @Inject constructor(
    private val addEventUseCase: AddEventUseCase,
    private val addPayeeUseCase: AddPayeeUseCase,
    private val addLocationUseCase: AddLocationUseCase
) {
    suspend operator fun invoke(
        event: Event?,
        payees: List<PayeeTransaction>,
        location: Location?
    ): PreparedData {
        val finalEvent = event?.let { 
            if (it.id < 0) persistEvent(it) else it 
        }
        val finalPayeeIds = payees.map { 
            if (it.id < 0) persistPayee(it) else it.id 
        }
        val finalLocation = location?.let { 
            if (it.id < 0) persistLocation(it) else it 
        }
        return PreparedData(finalEvent, finalPayeeIds, finalLocation)
    }
    
    private suspend fun persistEvent(event: Event): Event {
        val id = addEventUseCase(...)
        return event.copy(id = id)
    }
    // ... similar for payee and location
}

data class PreparedData(
    val event: Event?,
    val payeeIds: List<Long>,
    val location: Location?
)
```

**Trong ViewModel:**

```kotlin
fun addTransaction(amount: Double, description: String?, createAt: Long) {
    viewModelScope.launch {
        val selectedCategory = _selectedCategory.value ?: return@launch
        val selectedAccount = _selectedAccount.value ?: return@launch
        
        setLoading()
        try {
            // ✅ Chỉ 1 dòng thay vì 30+ dòng
            val preparedData = prepareTransactionDataUseCase(
                _selectedEvent.value,
                _selectedPayees.value,
                _selectedLocation.value
            )
            
            // Rest of logic...
        }
    }
}
```

**Lợi ích:**

- ✅ Giảm `addTransaction()` từ 90 dòng → 30 dòng
- ✅ Dễ test persistence logic riêng
- ✅ ViewModel chỉ focus vào orchestration
- ✅ Có thể reuse cho edit transaction

**Effort:** 1-2 giờ

---

## ✅ **CÁCH 2: Group Related State vào Data Class** (Rất đơn giản)

### Vấn đề:

- 12 StateFlow riêng lẻ → khó quản lý, dễ miss update
- Không có single source of truth

### Giải pháp:

Group các state liên quan vào data class, chỉ expose 1 StateFlow chính.

```kotlin
// Trong ViewModel
data class TransactionFormState(
    val transactionId: Long? = null,
    val selectedCategory: Category? = null,
    val selectedAccount: Account? = null,
    val selectedEvent: Event? = null,
    val selectedPayees: List<PayeeTransaction> = emptyList(),
    val selectedLocation: Location? = null,
    val transactionImage: TransactionImage? = null,
    val temporaryEvents: List<Event> = emptyList(),
    val temporaryPayees: List<PayeeTransaction> = emptyList(),
    val temporaryLocations: List<Location> = emptyList()
)

private val _formState = MutableStateFlow(TransactionFormState())
val formState = _formState.asStateFlow()

// Expose individual cho backward compatibility (nếu cần)
val selectedCategory = _formState.map { it.selectedCategory }.asStateFlow()
val selectedAccount = _formState.map { it.selectedAccount }.asStateFlow()
// ... etc
```

**Update methods:**

```kotlin
fun selectCategory(category: Category) {
    _formState.update { it.copy(selectedCategory = category) }
}

fun selectAccount(account: Account) {
    _formState.update { it.copy(selectedAccount = account) }
}
```

**Lợi ích:**

- ✅ Single source of truth
- ✅ Dễ snapshot/restore state
- ✅ Dễ debug (chỉ cần log 1 object)
- ✅ Có thể serialize để save/restore

**Effort:** 2-3 giờ

---

## ✅ **CÁCH 3: Extract Temporary Data Logic vào Extension Functions** (Đơn giản)

### Vấn đề:

- Logic tìm temporary data lặp lại 3 lần (event, payee, location)
- Code duplicate

### Giải pháp:

Tạo extension functions hoặc helper object.

```kotlin
// Trong ViewModel hoặc separate file
private object TemporaryDataHelper {
    fun generateId(offset: Int): Long = -(offset + 1).toLong()
    
    fun <T> findTemporary(
        items: List<T>,
        id: Long,
        getId: (T) -> Long
    ): T? {
        if (id >= 0) return null
        return items.find { getId(it) == id }
    }
}

// Sử dụng:
fun selectEventById(eventId: Long) {
    viewModelScope.launch {
        val event = if (eventId < 0) {
            TemporaryDataHelper.findTemporary(
                _temporaryEvents.value,
                eventId,
                Event::id
            )
        } else {
            getEventByIdUseCase(eventId)
        }
        event?.let { _selectedEvent.value = it }
    }
}
```

**Hoặc đơn giản hơn - inline helper:**

```kotlin
private inline fun <T> findById(
    id: Long,
    temporaryList: List<T>,
    getId: (T) -> Long,
    fetchById: suspend (Long) -> T?
): T? {
    return if (id < 0) {
        temporaryList.find { getId(it) == id }
    } else {
        runBlocking { fetchById(id) }
    }
}
```

**Lợi ích:**

- ✅ Giảm duplicate code
- ✅ Dễ maintain
- ✅ Consistent logic

**Effort:** 1 giờ

---

## ✅ **CÁCH 4: Extract Image Management vào Separate Class** (Trung bình)

### Vấn đề:

- Image logic (save, delete) chiếm ~50 dòng
- Có thể tách riêng để reuse

### Giải pháp:

Tạo `TransactionImageManager` class.

```kotlin
class TransactionImageManager @Inject constructor(
    private val saveImageUseCase: SaveTransactionImageUseCase,
    private val deleteImageUseCase: DeleteTransactionImagesUseCase
) {
    suspend fun saveImage(
        imageUri: Uri,
        oldImage: TransactionImage?
    ): Result<TransactionImage> {
        return try {
            oldImage?.let { deleteImageUseCase.deleteSingle(it) }
            saveImageUseCase(imageUri).getOrThrow()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteImage(image: TransactionImage): Result<Unit> {
        return try {
            deleteImageUseCase.deleteSingle(image)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Trong ViewModel:**

```kotlin
private val imageManager = TransactionImageManager(...)

fun saveImage(imageUri: Uri) {
    viewModelScope.launch {
        _imageUploadState.value = UIState.Loading
        imageManager.saveImage(imageUri, _transactionImage.value)
            .onSuccess { image ->
                _transactionImage.value = image
                _imageUploadState.value = UIState.Success(image)
            }
            .onFailure { error ->
                _imageUploadState.value = UIState.Error(error.message ?: "Failed")
            }
    }
}
```

**Lợi ích:**

- ✅ Tách biệt image logic
- ✅ Có thể test riêng
- ✅ Có thể reuse ở nơi khác

**Effort:** 2 giờ

---

## ✅ **CÁCH 5: Consolidate Selection Methods** (Đơn giản)

### Vấn đề:

- 3 methods tương tự: `selectEventById`, `selectPayeeByIds`, `selectLocationById`
- Pattern giống nhau: check temporary → fetch → update state

### Giải pháp:

Tạo generic selection method hoặc sealed class.

```kotlin
sealed class SelectionType {
    data class Event(val id: Long) : SelectionType()
    data class Payees(val ids: LongArray) : SelectionType()
    data class Location(val id: Long) : SelectionType()
}

private suspend fun handleSelection(type: SelectionType) {
    when (type) {
        is SelectionType.Event -> {
            val event = findByIdOrTemporary(
                type.id,
                _temporaryEvents.value,
                Event::id,
                ::getEventByIdUseCase
            )
            event?.let { _selectedEvent.value = it }
        }
        // ... similar for others
    }
}
```

**Hoặc đơn giản hơn - chỉ refactor pattern:**

```kotlin
private suspend fun <T> selectById(
    id: Long,
    temporaryList: List<T>,
    getId: (T) -> Long,
    fetchUseCase: suspend (Long) -> T?,
    updateState: (T) -> Unit
) {
    val item = if (id < 0) {
        temporaryList.find { getId(it) == id }
    } else {
        fetchUseCase(id)
    }
    item?.let(updateState)
}
```

**Lợi ích:**

- ✅ Giảm duplicate
- ✅ Consistent error handling

**Effort:** 1-2 giờ

---

## ✅ **CÁCH 6: Extract Navigation Logic** (Rất đơn giản)

### Vấn đề:

- 7 navigation methods chỉ là wrapper đơn giản

### Giải pháp:

Group vào object hoặc extension.

```kotlin
private val navigation = object {
    fun toCategory() = navigator.navigateToMoreCategory()
    fun toAccount(id: Long = -1L) = navigator.navigateToSelectAccount(id)
    fun toEvent(id: Long = -1L) = navigator.navigateToSelectEvent(id)
    fun toPayee(ids: LongArray = longArrayOf()) = navigator.navigateToSelectPayee(ids)
    fun toLocation(id: Long = -1L) = navigator.navigateToSelectLocation(id)
}

// Sử dụng:
fun toSelectAccount() {
    navigation.toAccount(_selectedAccount.value?.id ?: -1L)
}
```

**Lợi ích:**

- ✅ Group related methods
- ✅ Dễ mock khi test

**Effort:** 30 phút

---

## ✅ **CÁCH 7: Use Sealed Class cho Form State** (Trung bình - khó hơn)

### Vấn đề:

- Không có type-safe state management
- Khó biết form đang ở trạng thái nào

### Giải pháp:

Sử dụng sealed class pattern (như đã có `AddTransactionUiState` trong project).

```kotlin
sealed class TransactionFormUiState {
    abstract val formData: TransactionFormData
    
    data class Initial(override val formData: TransactionFormData) : TransactionFormUiState()
    data class Editing(override val formData: TransactionFormData) : TransactionFormUiState()
    data class Saving(override val formData: TransactionFormData) : TransactionFormUiState()
    data class Saved(override val formData: TransactionFormData, val transactionId: Long) : TransactionFormUiState()
    data class Error(override val formData: TransactionFormData, val message: String) : TransactionFormUiState()
}

data class TransactionFormData(
    val category: Category? = null,
    val account: Account? = null,
    // ... all other fields
)
```

**Lợi ích:**

- ✅ Type-safe state
- ✅ Compiler enforce đúng state
- ✅ Dễ handle UI updates

**Effort:** 3-4 giờ

---

## ✅ **CÁCH 8: Split thành 2 ViewModels** (Phức tạp hơn, nhưng tốt nhất)

### Vấn đề:

- ViewModel quá lớn, quá nhiều responsibilities

### Giải pháp:

Split thành:

1. `AddTransactionViewModel` - Main transaction logic
2. `TransactionFormViewModel` - Form state management (có thể share state)

**Hoặc:**

1. `AddTransactionViewModel` - Core transaction
2. `TransactionSelectionViewModel` - Handle selections (category, account, event, etc.)

```kotlin
// Option 1: Share state qua SavedStateHandle hoặc parent ViewModel
class TransactionSelectionViewModel @Inject constructor(
    // ... only selection-related use cases
) : ViewModel() {
    // Chỉ handle: category, account, event, payee, location selection
}

class AddTransactionViewModel @Inject constructor(
    private val selectionViewModel: TransactionSelectionViewModel,
    // ... transaction use cases
) : ViewModel() {
    // Chỉ handle: save transaction, image, validation
}
```

**Lợi ích:**

- ✅ Single Responsibility
- ✅ Dễ test từng phần
- ✅ Có thể reuse selection logic

**Effort:** 1 ngày

---

## 🎯 **KHUYẾN NGHỊ THỨ TỰ THỰC HIỆN**

### **Phase 1 - Quick Wins (1 ngày):**

1. ✅ **CÁCH 1**: Extract `PrepareTransactionDataUseCase`
2. ✅ **CÁCH 3**: Extract temporary data helper
3. ✅ **CÁCH 6**: Extract navigation logic

**Kết quả:** Giảm ~100 dòng, dễ đọc hơn nhiều

### **Phase 2 - Structure (2-3 ngày):**

4. ✅ **CÁCH 2**: Group state vào data class
5. ✅ **CÁCH 4**: Extract image manager
6. ✅ **CÁCH 5**: Consolidate selection methods

**Kết quả:** Code structure tốt hơn, dễ maintain

### **Phase 3 - Advanced (Optional):**

7. ✅ **CÁCH 7**: Sealed class state (nếu cần type safety)
8. ✅ **CÁCH 8**: Split ViewModels (nếu team lớn, cần scale)

---

## 📊 **SO SÁNH CÁC CÁCH**

| Cách                     | Độ khó        | Effort | Impact | Nên làm?               |
|--------------------------|---------------|--------|--------|------------------------|
| 1. Extract UseCase       | ⭐ Dễ          | 1-2h   | ⭐⭐⭐⭐⭐  | ✅ **Nên làm đầu tiên** |
| 2. Group State           | ⭐ Dễ          | 2-3h   | ⭐⭐⭐⭐   | ✅ Nên làm              |
| 3. Helper Functions      | ⭐ Dễ          | 1h     | ⭐⭐⭐    | ✅ Nên làm              |
| 4. Image Manager         | ⭐⭐ Trung bình | 2h     | ⭐⭐⭐    | ✅ Có thể làm           |
| 5. Consolidate Selection | ⭐ Dễ          | 1-2h   | ⭐⭐     | ⚠️ Tùy chọn            |
| 6. Navigation Group      | ⭐ Rất dễ      | 30ph   | ⭐⭐     | ⚠️ Tùy chọn            |
| 7. Sealed Class          | ⭐⭐⭐ Khó       | 3-4h   | ⭐⭐⭐⭐   | ⚠️ Nếu cần type safety |
| 8. Split ViewModels      | ⭐⭐⭐⭐ Rất khó  | 1 ngày | ⭐⭐⭐⭐⭐  | ⚠️ Chỉ khi thực sự cần |

---

## 💡 **KẾT LUẬN**

**Cách tốt nhất cho project hiện tại:**

1. **CÁCH 1** (Extract UseCase) - **BẮT BUỘC** làm đầu tiên
2. **CÁCH 2** (Group State) - **NÊN LÀM** để có structure tốt
3. **CÁCH 3** (Helper Functions) - **NÊN LÀM** để giảm duplicate

Sau 3 cách này, ViewModel sẽ giảm từ **490 dòng → ~300 dòng**, dễ đọc và maintain hơn nhiều.

**Các cách khác làm sau nếu cần.**

---

*Tài liệu được tạo dựa trên best practices của senior Android developers*
