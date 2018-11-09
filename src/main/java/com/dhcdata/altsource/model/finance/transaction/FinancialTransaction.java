package com.dhcdata.altsource.model.finance.transaction;

import com.dhcdata.altsource.model.finance.account.TransactionAccount;

import org.joda.money.Money;

public interface FinancialTransaction {
    public Long getId();

    public void setId(Long id);

    public Transaction getTransaction();

    public void setTransaction(Transaction type);

    public TransactionAccount getAccount();

    public void setAccount(TransactionAccount account);

    public Money getPreviousBalance();

    public void setPreviousBalance(Money amount);

    public Money getPostBalance();

    public void setPostBalance(Money amount);

}