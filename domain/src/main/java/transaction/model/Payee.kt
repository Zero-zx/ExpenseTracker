package transaction.model

import account.model.Account

data class Payee(
    val id: Long = 0,
    val eventId: Long,
    val account: Account?,
    val participantName: String
)

