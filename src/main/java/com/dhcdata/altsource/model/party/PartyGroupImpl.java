package com.dhcdata.altsource.model.party;

import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class PartyGroupImpl extends PartyImpl implements PartyGroup, NamedParty {

    private String groupName;

    public String getName() {
        return groupName;
    }
}
