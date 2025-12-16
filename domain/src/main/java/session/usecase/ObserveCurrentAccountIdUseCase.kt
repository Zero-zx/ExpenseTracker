package session.usecase

import kotlinx.coroutines.flow.Flow
import session.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case to observe changes to the currently selected account
 */
class ObserveCurrentAccountIdUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<Long?> {
        return sessionRepository.observeCurrentAccountId()
    }
}

