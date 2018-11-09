package com.dhcdata.altsource.model.finance.account;

import java.util.Date;

public interface Account {
    public boolean isOpen();

    public Long getId();

    public Date getOpenDate();

    public Date getClosedDate();
}