package com.dhcdata.altsource.web;

import com.dhcdata.altsource.BankService;
import com.dhcdata.altsource.controller.WebController;
import com.dhcdata.altsource.controller.webconfig.CustomAuthenticationProvider;
import com.dhcdata.altsource.controller.webconfig.LoggingAccessDeniedHandler;
import com.dhcdata.altsource.controller.webconfig.WebSecurityConfig;
import com.dhcdata.altsource.model.finance.FinanceFactory;
import com.dhcdata.altsource.model.party.PartyFactory;
import com.dhcdata.altsource.model.user.UserFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { WebSecurityConfig.class, CustomAuthenticationProvider.class, UserFactory.class,
        FinanceFactory.class, PartyFactory.class, LoggingAccessDeniedHandler.class, WebController.class,
        BankService.class })
@WebAppConfiguration
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void verifiesAllPagesLoad() throws Exception {
        verifiesPageLoads("/", "login");
        verifiesPageLoads("/login", "login");
    }

    public void verifiesPageLoads(String req, String viewName) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(req)).andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.view().name(viewName))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
