package com.dhcdata.altsource.model.finance.transaction;

import java.util.Date;

import org.joda.money.Money;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitialTransactionImpl implements Transaction {
    private Long id;
    private static final String TYPE = "INITIAL";
    private Date date = new Date();
    private Money amount;

    public Money applyTo(Money amount) {
        return amount.plus(this.amount);
    }

    public String getType() {
        return TYPE;
    }
}