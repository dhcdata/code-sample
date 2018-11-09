package com.dhcdata.altsource.model.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.dhcdata.altsource.model.party.NamedParty;

import org.springframework.stereotype.Service;

@Service
public class UserFactory {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public User createUser(String userName, String password, NamedParty party) {
        User user = new UserImpl(userName, password, party);
        users.put(user.getUserId(), user);

        return user;
    }

    public boolean userExists(String userName) {
        return users.containsKey(userName);
    }

    public User login(String userName, String password) {
        if (validCredentials(userName, password)) {
            return users.get(userName);
        }
        return null;
    }

    public boolean validCredentials(String userName, String password) {
        return this.users.values().stream().filter(u -> checkCredentials(u, userName, password))
                .collect(Collectors.reducing((a, b) -> null)).isPresent();

    }

    private boolean checkCredentials(User u, String userName, String password) {
        return u.getUserId().equalsIgnoreCase(userName) && u.getPassword().equals(password);
    }

    public Map<String, User> getUsers() {
        return users;
    }

}