package presentation;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import navigation.Navigator;
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
public final class ReportsViewModel_Factory implements Factory<ReportsViewModel> {
  private final Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider;

  private final Provider<Navigator> navigatorProvider;

  private ReportsViewModel_Factory(
      Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider,
      Provider<Navigator> navigatorProvider) {
    this.getTransactionsByDateRangeUseCaseProvider = getTransactionsByDateRangeUseCaseProvider;
    this.navigatorProvider = navigatorProvider;
  }

  @Override
  public ReportsViewModel get() {
    return newInstance(getTransactionsByDateRangeUseCaseProvider.get(), navigatorProvider.get());
  }

  public static ReportsViewModel_Factory create(
      Provider<GetTransactionsByDateRangeUseCase> getTransactionsByDateRangeUseCaseProvider,
      Provider<Navigator> navigatorProvider) {
    return new ReportsViewModel_Factory(getTransactionsByDateRangeUseCaseProvider, navigatorProvider);
  }

  public static ReportsViewModel newInstance(
      GetTransactionsByDateRangeUseCase getTransactionsByDateRangeUseCase, Navigator navigator) {
    return new ReportsViewModel(getTransactionsByDateRangeUseCase, navigator);
  }
}
