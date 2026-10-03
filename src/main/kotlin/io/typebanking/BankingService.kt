package io.typebanking

import java.math.BigDecimal

/** Module 00 deliberately contains common production smells. Do not “fix” it yet. */
data class Account(
    val id: String,
    var balance: BigDecimal,
    var status: String,
)

class BankingService {
    fun deposit(account: Account, amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "invalid" }
        account.balance += amount
    }

    fun withdraw(account: Account, amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "invalid" }
        check(account.status == "ACTIVE") { "invalid" }
        check(account.balance >= amount) { "invalid" }
        account.balance -= amount
    }
}

