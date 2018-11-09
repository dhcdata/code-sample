package com.dhcdata.altsource.model.finance.transaction;

import com.dhcdata.altsource.model.finance.account.TransactionAccount;

import org.joda.money.Money;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionImpl implements FinancialTransaction {
    private Long id;
    private Transaction transaction;
    private TransactionAccount account;
    private Money previousBalance;
    private Money postBalance;

}
