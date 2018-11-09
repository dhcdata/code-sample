package com.dhcdata.altsource.model.finance;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.dhcdata.altsource.model.finance.account.TransactionAccount;
import com.dhcdata.altsource.model.finance.account.TransactionAccountImpl;
import com.dhcdata.altsource.model.finance.transaction.DepositTransactionImpl;
import com.dhcdata.altsource.model.finance.transaction.FinancialTransaction;
import com.dhcdata.altsource.model.finance.transaction.FinancialTransactionImpl;
import com.dhcdata.altsource.model.finance.transaction.InitialTransactionImpl;
import com.dhcdata.altsource.model.finance.transaction.Transaction;
import com.dhcdata.altsource.model.finance.transaction.WithdrawalTransactionImpl;
import com.dhcdata.altsource.model.party.NamedParty;
import com.dhcdata.altsource.model.party.Party;

import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FinanceFactory {

    private static Logger log = LoggerFactory.getLogger(FinanceFactory.class);

    private AtomicLong accountIds = new AtomicLong();
    private AtomicLong transactionIds = new AtomicLong();

    // TODO: move to DAO
    private final Map<Long, FinancialTransaction> transactions = new ConcurrentHashMap<>();
    private final Map<Long, TransactionAccount> accounts = new ConcurrentHashMap<>();

    public TransactionAccount createTransactionAccount(Money openingBalance, NamedParty party) {
        TransactionAccount account = new TransactionAccountImpl(accountIds.incrementAndGet(),
                Money.of(openingBalance.getCurrencyUnit(), 0.00), new Date(), null, party);

        // TODO: this should fail if the initial transaction fails.
        getFinancialTransactionBuilder("INITIAL", openingBalance, account).create();

        accounts.put(account.getId(), account);
        return account;

    }

    public Builder getFinancialTransactionBuilder(String transactionType, Money amount, TransactionAccount account) {
        return new Builder().setTransaction(createTransaction(transactionType, amount)).setAccount(account);

    }

    public Optional<FinancialTransaction> createFinancialTransaction(String transactionType, Money amount,
            TransactionAccount account) {
        return new Builder().setTransaction(createTransaction(transactionType, amount)).setAccount(account).create();
    }

    public Transaction createTransaction(String transactionType, Money amount) {
        Transaction txn = new InitialTransactionImpl();
        switch (transactionType) {
        case "WITHDRAW":
            txn = new WithdrawalTransactionImpl();
            break;
        case "DEPOSIT":
            txn = new DepositTransactionImpl();
            break;
        default:
            break;
        }
        txn.setAmount(amount);
        return txn;
    }

    public Long createTransactionId() {
        return transactionIds.incrementAndGet();
    }

    public Collection<FinancialTransaction> getAccountTransactions(TransactionAccount account) {

        return this.transactions.values().stream().filter(t -> t.getAccount().equals(account))
                .collect(Collectors.toCollection(LinkedList<FinancialTransaction>::new));
    }

    public Collection<TransactionAccount> getPartyAccounts(Party party) {
        return accounts.values().stream().filter(a -> a.getOwner().equals(party)).collect(Collectors.toList());

    }

    public TransactionAccount getAccount(Long id) {
        return accounts.get(id);
    }

    public class Builder {
        private Transaction transaction;
        private TransactionAccount account;

        public Builder() {
            // Using the builder pattern to allow flexibility in the configuration of the
            // transaction.
        }

        public Builder setTransaction(Transaction type) {
            this.transaction = type;
            return this;
        }

        public Builder setAccount(TransactionAccount account) {
            this.account = account;
            return this;
        }

        public Transaction getTransaction() {
            return this.transaction;
        }

        public TransactionAccount getAccount() {
            return this.account;
        }

        public boolean isBuildReady() {
            return this.transaction != null && this.account != null;
        }

        /*
         * Builder is needed to pass around a transaction object to setup. Create is
         * needed to actually create that transaction object. syncronized is needed
         * because we're updating the balance on the account and might otherwise have a
         * race condition. TODO: ensure that 'syncronized' is sufficient to deal with
         * the race condition.
         */
        public synchronized Optional<FinancialTransaction> create() {
            if (!isBuildReady()) {

                log.error("Transaction has not been built correctly");
                // TODO: instead of empty, create a "Failed Transaction" so that it gets logged.
                return Optional.empty();
            }

            FinancialTransaction finTxn = new FinancialTransactionImpl();

            finTxn.setTransaction(this.transaction);
            finTxn.setAccount(this.account);
            finTxn.setPreviousBalance(this.account.getBalance());

            if (transaction.getId() == null)
                transaction.setId(createTransactionId());
            Money newBalance = finTxn.getTransaction().applyTo(this.account.getBalance());
            finTxn.setPostBalance(newBalance);
            finTxn.getAccount().setBalance(newBalance);
            transactions.put(transaction.getId(), finTxn);

            return Optional.of(finTxn);

        }

    }

    public Collection<TransactionAccount> getAccounts() {
        return accounts.values();
    }
}
