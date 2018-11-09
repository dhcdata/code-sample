package com.dhcdata.altsource.model.user;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.dhcdata.altsource.model.party.NamedParty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImpl implements User {
    private String userId;
    private String password;
    private NamedParty party;

}
