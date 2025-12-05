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
public final class AddAccountUseCase_Factory implements Factory<AddAccountUseCase> {
  private final Provider<AccountRepository> repositoryProvider;

  private AddAccountUseCase_Factory(Provider<AccountRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddAccountUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddAccountUseCase_Factory create(Provider<AccountRepository> repositoryProvider) {
    return new AddAccountUseCase_Factory(repositoryProvider);
  }

  public static AddAccountUseCase newInstance(AccountRepository repository) {
    return new AddAccountUseCase(repository);
  }
}
