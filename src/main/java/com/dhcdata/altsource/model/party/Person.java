package com.dhcdata.altsource.model.party;

import java.util.Date;

public interface Person extends NamedParty {
    public String getFirstName();

    public String getLastName();

    public Date getBirthDate();

    public String getGender();
}
