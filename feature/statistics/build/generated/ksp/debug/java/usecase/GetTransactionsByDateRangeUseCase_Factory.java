package usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import transaction.repository.TransactionRepository;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class GetTransactionsByDateRangeUseCase_Factory implements Factory<GetTransactionsByDateRangeUseCase> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private GetTransactionsByDateRangeUseCase_Factory(
      Provider<TransactionRepository> transactionRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
  }

  @Override
  public GetTransactionsByDateRangeUseCase get() {
    return newInstance(transactionRepositoryProvider.get());
  }

  public static GetTransactionsByDateRangeUseCase_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider) {
    return new GetTransactionsByDateRangeUseCase_Factory(transactionRepositoryProvider);
  }

  public static GetTransactionsByDateRangeUseCase newInstance(
      TransactionRepository transactionRepository) {
    return new GetTransactionsByDateRangeUseCase(transactionRepository);
  }
}
