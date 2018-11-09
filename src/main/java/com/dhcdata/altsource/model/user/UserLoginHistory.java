package com.dhcdata.altsource.model.user;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings(value = "unused")
class UserLoginHistory implements LoginHistory {
    private Long id;
    private User user;
    private Date loginDate;
    private Date logoutDate;
    private boolean loggedIn;

}
