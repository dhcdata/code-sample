package com.dhcdata.altsource.model.party;

import java.util.Date;

import lombok.Data;

@Data
public class PersonImpl extends PartyImpl implements Person, NamedParty {
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;

    public PersonImpl(Long id, String firstName, String lastName) {
        this.setId(id);
        this.setPartyType("PERSON");
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = new Date();
    }

    public PersonImpl(Long id, String firstName, String lastName, Date birthDate, String gender) {
        this.setId(id);
        this.setPartyType("PERSON");
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

}
