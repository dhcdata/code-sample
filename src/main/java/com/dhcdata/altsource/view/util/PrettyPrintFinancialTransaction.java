package com.dhcdata.altsource.view.util;

import java.util.Date;

import com.dhcdata.altsource.controller.shell.provider.ConsoleWriter;
import com.dhcdata.altsource.model.finance.transaction.FinancialTransaction;
import org.joda.money.Money;

import lombok.Data;

@Data
public class PrettyPrintFinancialTransaction implements PrettyClass {

    private Long accountId;
    private Long transactionId;
    private Date transactionDate;
    private String transactionType;
    private Money amount;
    private Money previousBalance;
    private Money postBalance;

    public PrettyPrintFinancialTransaction(FinancialTransaction transaction) {
        this.accountId = transaction.getAccount().getId();
        this.transactionId = transaction.getTransaction().getId();
        this.transactionDate = transaction.getTransaction().getDate();
        this.transactionType = transaction.getTransaction().getType();
        this.amount = transaction.getTransaction().getAmount();
        this.previousBalance = transaction.getPreviousBalance();
        this.postBalance = transaction.getPostBalance();
    }

    @Override
    public String[] toStringArray() {
        return new String[] { Long.toString(this.accountId), Long.toString(this.transactionId),
                this.transactionDate.toString(), this.transactionType, this.amount.toString(),
                this.previousBalance.toString(), this.postBalance.toString() };

    }

    public static String[] getHeader() {
        return new String[] { "AccountId", "TxnId", "Date", "Type", "Amount", "Previous Bal", "Balance" };
    }

    public static Class<FinancialTransaction> getType() {
        return FinancialTransaction.class;
    }
}