package com.dhcdata.altsource.model.finance.transaction;

import java.util.Date;

import org.joda.money.Money;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalTransactionImpl implements Transaction {
    private Long id;
    private static final String TYPE = "WITHDRAW";
    private Date date = new Date();
    private Money amount;

    public Money applyTo(Money amount) {
        return amount.minus(this.amount);
    }

    public String getType() {
        return TYPE;
    }
}