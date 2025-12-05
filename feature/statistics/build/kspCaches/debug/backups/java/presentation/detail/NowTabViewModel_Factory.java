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
public final class NowTabViewModel_Factory implements Factory<NowTabViewModel> {
  private final Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider;

  private NowTabViewModel_Factory(
      Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider) {
    this.getTransactionsByDateRangeUseCaseProvider = getTransactionsByDateRangeUseCaseProvider;
  }

  @Override
  public NowTabViewModel get() {
    return newInstance(getTransactionsByDateRangeUseCaseProvider.get());
  }

  public static NowTabViewModel_Factory create(
      Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider) {
    return new NowTabViewModel_Factory(getTransactionsByDateRangeUseCaseProvider);
  }

  public static NowTabViewModel newInstance(
      GetTransactionsByDateRangeUseCase getTransactionsByDateRangeUseCase) {
    return new NowTabViewModel(getTransactionsByDateRangeUseCase);
  }
}
