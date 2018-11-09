package com.dhcdata.altsource.model.finance.account;

import java.util.Date;

import com.dhcdata.altsource.model.party.Party;

import org.joda.money.Money;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAccountImpl implements TransactionAccount {
    private Long id;
    private Money balance;
    private Date openDate;
    private Date closedDate;
    private Party owner;

    @Override
    public boolean isOpen() {
        Date nowDate = new Date();
        return openDate.before(nowDate) && (closedDate == null || closedDate.after(nowDate));
    }

    @Override
    public void deposit(Money money) {
        this.balance = this.balance.plus(money);
    }

    @Override
    public void withdraw(Money money) {
        this.balance = this.balance.minus(money);
    }

}
