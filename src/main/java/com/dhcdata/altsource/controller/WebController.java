package com.dhcdata.altsource.controller;

import java.math.BigDecimal;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.dhcdata.altsource.BankService;
import com.dhcdata.altsource.model.finance.account.TransactionAccount;
import com.dhcdata.altsource.model.finance.transaction.FinancialTransaction;
import com.dhcdata.altsource.model.party.NamedParty;
import com.dhcdata.altsource.model.user.User;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAW = "WITHDRAW";
    private static final String LOGIN_VIEW = "login";
    private static final String TXN_VIEW = "transactions";

    private static final String TXN_KEY = "transactions";
    private static final String USER_KEY = "user";
    private static final String ACCT_KEY = "account";

    BankService bankService;

    WebController(BankService bankService) {
        this.bankService = bankService;
    }

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

        setData(model, request);
        return getUserId(request) == null ? LOGIN_VIEW : TXN_VIEW;
    }

    @RequestMapping("/createAccount")
    public String createAccount(@RequestParam(name = "amount", required = false) BigDecimal amount,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "password", required = false) String password, Model model,
            HttpServletRequest request) {

        if (amount == null)
            return "createAccount";

        bankService.createAccount(userId, password, firstName, lastName, amount);
        // Should the process have the newly created account autologin?

        setData(model, request);
        return "redirect:/showTransactions";
    }

    @RequestMapping(value = { "/login", "/logout" })
    public String login(Model model, HttpServletRequest request) {

        setData(model, request);
        return LOGIN_VIEW;
    }

    @RequestMapping("/deposit")
    public String recordDeposit(@RequestParam(name = "amount", required = false) BigDecimal amount, Model model,
            HttpServletRequest request) {

        if (amount != null) {
            TransactionAccount account = getAccount(request);
            Optional<FinancialTransaction> txn = bankService.recordDeposit(account, amount);
            model.addAttribute("message", bankService.createMessage(txn, DEPOSIT));
        }

        setData(model, request);
        return TXN_VIEW;
    }

    @RequestMapping("/withdraw")
    public String recordWithdrawal(@RequestParam(name = "amount", required = false) BigDecimal amount, Model model,
            HttpServletRequest request) {

        if (amount != null) {
            TransactionAccount account = getAccount(request);
            Optional<FinancialTransaction> txn = bankService.recordWithdrawal(account, amount);
            model.addAttribute("message", bankService.createMessage(txn, WITHDRAW));
        }

        setData(model, request);
        return TXN_VIEW;
    }

    @RequestMapping("/showTransactions")
    public String showTransactions(HttpServletRequest request, Model model) {
        setData(model, request);
        return TXN_VIEW;
    }

    // view method
    private void setData(Model model, HttpServletRequest request) {

        model.addAttribute(USER_KEY, null);
        model.addAttribute(ACCT_KEY, Optional.empty());
        model.addAttribute(TXN_KEY, null);

        if (getSecurityContext(request) != null) {
            User user = bankService.getUser(getUserId(request));
            NamedParty party = bankService.getParty(user);

            Optional<TransactionAccount> account = bankService.getFirstAccount(party);

            model.addAttribute(USER_KEY, user);
            model.addAttribute(ACCT_KEY, account);
            account.ifPresent(a -> model.addAttribute(TXN_KEY, bankService.getAccountTransactions(a)));
        }
    }

    // Readability helper methods

    private TransactionAccount getAccount(HttpServletRequest request) {

        if (getSecurityContext(request) == null) {
            return null;
        }

        User user = bankService.getUser(getUserId(request));
        NamedParty party = bankService.getParty(user);
        Optional<TransactionAccount> account = bankService.getFirstAccount(party);

        return account.isPresent() ? account.get() : null;

    }

    private SecurityContext getSecurityContext(HttpServletRequest request) {
        return (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
    }

    private String getUserId(HttpServletRequest request) {

        SecurityContext sc = getSecurityContext(request);
        return sc == null ? null : sc.getAuthentication().getPrincipal().toString();

    }
}