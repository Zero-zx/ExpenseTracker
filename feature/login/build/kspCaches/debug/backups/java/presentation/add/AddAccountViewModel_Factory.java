package presentation.add;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import domain.usecase.AddAccountUseCase;
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
public final class AddAccountViewModel_Factory implements Factory<AddAccountViewModel> {
  private final Provider<AddAccountUseCase> addAccountUseCaseProvider;

  private AddAccountViewModel_Factory(Provider<AddAccountUseCase> addAccountUseCaseProvider) {
    this.addAccountUseCaseProvider = addAccountUseCaseProvider;
  }

  @Override
  public AddAccountViewModel get() {
    return newInstance(addAccountUseCaseProvider.get());
  }

  public static AddAccountViewModel_Factory create(
      Provider<AddAccountUseCase> addAccountUseCaseProvider) {
    return new AddAccountViewModel_Factory(addAccountUseCaseProvider);
  }

  public static AddAccountViewModel newInstance(AddAccountUseCase addAccountUseCase) {
    return new AddAccountViewModel(addAccountUseCase);
  }
}
