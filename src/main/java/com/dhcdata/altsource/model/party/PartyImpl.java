package com.dhcdata.altsource.model.party;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyImpl implements NamedParty {
    private Long id;
    private String partyType;
    private String name;
}
