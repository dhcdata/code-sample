package com.dhcdata.altsource.model.user;

import java.util.Date;

public interface LoginHistory {
    public Long getId();

    public User getUser();

    public Date getLoginDate();

    public Date getLogoutDate();

    public boolean isLoggedIn();
}
