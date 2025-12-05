package presentation.detail;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import usecase.GetTransactionsByDateRangeUseCase;

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
public final class ChartTabViewModel_Factory implements Factory<ChartTabViewModel> {
  private final Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider;

  private ChartTabViewModel_Factory(
      Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider) {
    this.getTransactionsByDateRangeUseCaseProvider = getTransactionsByDateRangeUseCaseProvider;
  }

  @Override
  public ChartTabViewModel get() {
    return newInstance(getTransactionsByDateRangeUseCaseProvider.get());
  }

  public static ChartTabViewModel_Factory create(
      Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider) {
    return new ChartTabViewModel_Factory(getTransactionsByDateRangeUseCaseProvider);
  }

  public static ChartTabViewModel newInstance(
      GetTransactionsByDateRangeUseCase getTransactionsByDateRangeUseCase) {
    return new ChartTabViewModel(getTransactionsByDateRangeUseCase);
  }
}
