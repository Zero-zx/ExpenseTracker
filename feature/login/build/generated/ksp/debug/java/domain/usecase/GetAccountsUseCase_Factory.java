package domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import domain.repository.AccountRepository;
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
public final class GetAccountsUseCase_Factory implements Factory<GetAccountsUseCase> {
  private final Provider<AccountRepository> accountRepositoryProvider;

  private GetAccountsUseCase_Factory(Provider<AccountRepository> accountRepositoryProvider) {
    this.accountRepositoryProvider = accountRepositoryProvider;
  }

  @Override
  public GetAccountsUseCase get() {
    return newInstance(accountRepositoryProvider.get());
  }

  public static GetAccountsUseCase_Factory create(
      Provider<AccountRepository> accountRepositoryProvider) {
    return new GetAccountsUseCase_Factory(accountRepositoryProvider);
  }

  public static GetAccountsUseCase newInstance(AccountRepository accountRepository) {
    return new GetAccountsUseCase(accountRepository);
  }
}
