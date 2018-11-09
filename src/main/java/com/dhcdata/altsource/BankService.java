package com.dhcdata.altsource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Collection;
import java.util.Map;

import com.dhcdata.altsource.constants.Messages;
import com.dhcdata.altsource.controller.shell.provider.ConsoleWriter;
import com.dhcdata.altsource.model.finance.FinanceFactory;
import com.dhcdata.altsource.model.finance.account.TransactionAccount;
import com.dhcdata.altsource.model.finance.transaction.FinancialTransaction;
import com.dhcdata.altsource.model.party.NamedParty;
import com.dhcdata.altsource.model.party.Party;
import com.dhcdata.altsource.model.party.PartyFactory;
import com.dhcdata.altsource.model.user.User;
import com.dhcdata.altsource.model.user.UserFactory;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Service;

// You have been tasked with writing the world’s greatest banking ledger. Please
// code a solution that can perform the following workflows through a console
// application (accessed via the command line):

// -Create a new account
// -Login
// -Record a deposit
// -Record a withdrawal
// -Check balance
// -See transaction history
// -Log out

// For additional credit, you may implement this through a web page. They don’t
// have to run at the same time, but if you would like to do that, feel free.

// C# is preferred but not required. Use whatever frameworks/libraries you wish,
// and just make sure they are included or available via via NuGet/npm/etc.
// Please use a temporary memory store (local cache) instead of creating an
// actual database, and don't spend much time on the UI (unless you love doing
// that).

@Service
public class BankService {

    FinanceFactory financeFactory;
    UserFactory userFactory;
    PartyFactory partyFactory;
    private static final CurrencyUnit USD = CurrencyUnit.USD;

    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAW = "WITHDRAW";

    BankService(FinanceFactory financeFactory, UserFactory userFactory, PartyFactory partyFactory) {
        this.financeFactory = financeFactory;
        this.userFactory = userFactory;
        this.partyFactory = partyFactory;
    }

    public Optional<TransactionAccount> createAccount(String userId, String password, String firstName, String lastName,
            BigDecimal openingBalance) {

        if (userFactory.userExists(userId)) {
            return Optional.empty();
        }

        NamedParty person = partyFactory.createNamedParty(firstName, lastName);
        userFactory.createUser(userId, password, person);
        TransactionAccount account = financeFactory.createTransactionAccount(Money.of(USD, openingBalance),
                (NamedParty) person);

        return Optional.ofNullable(account);

    }

    public Optional<User> login(String userName, String password) {
        return Optional.ofNullable(userFactory.login(userName, password));
    }

    public Optional<FinancialTransaction> recordDeposit(TransactionAccount account, BigDecimal amount) {
        return recordTransaction(account, amount, DEPOSIT);
    }

    public Optional<FinancialTransaction> recordWithdrawal(TransactionAccount account, BigDecimal amount) {
        return recordTransaction(account, amount, WITHDRAW);
    }

    public Money checkBalance(Party party) {
        Optional<TransactionAccount> acct = getFirstAccount(party);
        return acct.isPresent() ? acct.get().getBalance() : Money.of(USD, 0);
    }

    public Collection<FinancialTransaction> showTransactions(TransactionAccount a) {
        return getAccountTransactions(a);
    }

    private Optional<FinancialTransaction> recordTransaction(TransactionAccount account, BigDecimal amount,
            String type) {
        return financeFactory.createFinancialTransaction(type, Money.of(USD, amount), account);
    }

    // Helper methods

    public User getUser(String userId) {
        Map<String, User> users = userFactory.getUsers();
        return userId == null || users.isEmpty() ? null : users.get(userId);
    }

    public NamedParty getParty(User user) {
        return user == null ? null : partyFactory.getNamedParty(user.getParty().getId());

    }

    public Optional<TransactionAccount> getFirstAccount(Party party) {
        return party == null ? Optional.empty() : financeFactory.getPartyAccounts(party).stream().findFirst();
    }

    public Collection<TransactionAccount> getPartyAccounts(Party party) {
        return financeFactory.getPartyAccounts(party);
    }

    public Collection<FinancialTransaction> getAccountTransactions(TransactionAccount a) {
        return financeFactory.getAccountTransactions(a);
    }

    public String createMessage(Optional<FinancialTransaction> txn, String type) {
        String message = "";
        String error = "";

        switch (type) {
        case DEPOSIT:
            message = Messages.DEPOSIT_AMOUNT;
            error = Messages.DEPOSIT_ERROR;
            break;
        case WITHDRAW:
            message = Messages.WITHDRAWN_AMOUNT;
            error = Messages.WITHDRAW_ERROR;
            break;
        default:
            break;
        }

        return txn.isPresent() ? String.format(message, txn.get().getTransaction().getAmount().getAmount().toString())
                : error;

    }
}