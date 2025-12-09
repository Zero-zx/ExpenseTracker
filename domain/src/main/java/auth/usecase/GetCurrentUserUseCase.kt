//package auth.usecase
//
//import auth.model.User
//import auth.repository.AuthRepository
//import javax.inject.Inject
//
//class GetCurrentUserUseCase @Inject constructor(
//    private val authRepository: AuthRepository
//) {
//    operator fun invoke(): User? {
//        return authRepository.getCurrentUser()
//    }
//}
//
