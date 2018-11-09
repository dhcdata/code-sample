package com.dhcdata.altsource.model.user;

import com.dhcdata.altsource.model.party.NamedParty;

public interface User {

    public String getUserId();

    public String getPassword();

    public NamedParty getParty();
}
