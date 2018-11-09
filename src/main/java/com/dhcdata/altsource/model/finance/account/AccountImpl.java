package com.dhcdata.altsource.model.finance.account;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountImpl implements Account {

    private Long id;
    private Date openDate;
    private Date closedDate;

    @Override
    public boolean isOpen() {
        Date nowDate = new Date();
        return openDate.before(nowDate) && (closedDate == null || closedDate.after(nowDate));
    }

}
