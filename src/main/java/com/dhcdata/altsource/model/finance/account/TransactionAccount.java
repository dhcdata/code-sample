package com.dhcdata.altsource.model.finance.account;

import com.dhcdata.altsource.model.party.Party;

import org.joda.money.Money;

public interface TransactionAccount extends Account {

    public Money getBalance();

    public void setBalance(Money amount);

    public void deposit(Money money);

    public void withdraw(Money money);

    public Party getOwner();

}
