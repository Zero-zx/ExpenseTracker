package presentation.list;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import domain.usecase.GetAccountsUseCase;
import javax.annotation.processing.Generated;

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
public final class AccountListViewModel_Factory implements Factory<AccountListViewModel> {
  private final Provider<GetAccountsUseCase> getAccountsUseCaseProvider;

  private AccountListViewModel_Factory(Provider<GetAccountsUseCase> getAccountsUseCaseProvider) {
    this.getAccountsUseCaseProvider = getAccountsUseCaseProvider;
  }

  @Override
  public AccountListViewModel get() {
    return newInstance(getAccountsUseCaseProvider.get());
  }

  public static AccountListViewModel_Factory create(
      Provider<GetAccountsUseCase> getAccountsUseCaseProvider) {
    return new AccountListViewModel_Factory(getAccountsUseCaseProvider);
  }

  public static AccountListViewModel newInstance(GetAccountsUseCase getAccountsUseCase) {
    return new AccountListViewModel(getAccountsUseCase);
  }
}
