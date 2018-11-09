package com.dhcdata.altsource.model.finance.transaction;

import java.util.Date;

import org.joda.money.Money;

public interface Transaction {
    public Long getId();

    public void setId(Long id);

    public String getType();

    public Date getDate();

    public void setDate(Date date);

    public Money getAmount();

    public void setAmount(Money amount);

    public Money applyTo(Money amount);
}