package com.dhcdata.altsource.model.party;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class PartyFactory {
    private AtomicLong partyIds = new AtomicLong();
    private final Map<Long, NamedParty> people = new ConcurrentHashMap<>();

    public NamedParty createNamedParty(String firstName, String lastName) {
        NamedParty party = new PersonImpl(partyIds.incrementAndGet(), firstName, lastName);
        people.put(party.getId(), party);

        return party;
    }

    public NamedParty getNamedParty(Long partyId) {
        return people.get(partyId);
    }
}