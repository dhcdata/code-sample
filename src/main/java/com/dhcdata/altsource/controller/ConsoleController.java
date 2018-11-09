package com.dhcdata.altsource.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dhcdata.altsource.AltsourceApplication;
import com.dhcdata.altsource.BankService;
import com.dhcdata.altsource.constants.Messages;
import com.dhcdata.altsource.controller.shell.Printer;
import com.dhcdata.altsource.controller.shell.provider.ConsoleWriter;
import com.dhcdata.altsource.model.finance.account.TransactionAccount;
import com.dhcdata.altsource.model.finance.transaction.FinancialTransaction;
import com.dhcdata.altsource.model.party.NamedParty;
import com.dhcdata.altsource.model.user.User;
import com.dhcdata.altsource.view.util.PrettyPrintFinancialTransaction;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.shell.Availability;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;
import org.springframework.stereotype.Component;

@ShellComponent
class ConsoleController {

    private BankService bank;

    @Autowired
    private Environment environment;

    private String userId;

    ConsoleController(BankService bank) {
        this.bank = bank;
    }

    @ShellMethodAvailability("alwaysAvailable")
    @ShellMethod(key = "create-account", value = Messages.DESC_CREATE_ACCOUNT_COMMAND, group = Messages.GROUP_USER_COMMANDS)
    public String createAccount(String userName, String password, String firstName, String lastName,
            BigDecimal openingBalance) {

        if (bank.createAccount(userName, password, firstName, lastName, openingBalance).isPresent()) {
            return ConsoleWriter.write(Messages.ACCOUNT_CREATED, login(userName, password));
        }

        return ConsoleWriter.write(Messages.USER_EXISTS_ERROR, userName);

    }

    @ShellMethodAvailability("notLoggedIn")
    @ShellMethod(key = "login", value = Messages.LOGIN_COMMAND_DESC, group = Messages.GROUP_USER_COMMANDS)
    public String login(String userName, String password) {
        Optional<User> loggedInUser = bank.login(userName, password);

        if (loggedInUser.isPresent()) {
            setSessionInfo(loggedInUser.get());
            return String.format(Messages.CONNECTED_USER, userName);
        }

        return ConsoleWriter.write(Messages.CREDENTIALS_ERROR, userName, password);
    }

    @ShellMethodAvailability("mustBeLoggedIn")
    @ShellMethod(key = "deposit", value = Messages.DESC_DEPOSIT_COMMAND, group = Messages.GROUP_BANK_COMMANDS)
    public String recordDeposit(BigDecimal amount) {

        User user = bank.getUser(this.userId);
        NamedParty party = bank.getParty(user);
        Optional<TransactionAccount> account = bank.getFirstAccount(party);
        Optional<FinancialTransaction> txn = account.isPresent() ? bank.recordDeposit(account.get(), amount)
                : Optional.empty();

        return txn.isPresent()
                ? ConsoleWriter.write(Messages.DEPOSIT_AMOUNT,
                        txn.get().getTransaction().getAmount().getAmount().toString())
                : ConsoleWriter.write(Messages.DEPOSIT_ERROR);
    }

    @ShellMethodAvailability("mustBeLoggedIn")
    @ShellMethod(key = "withdraw", value = Messages.DESC_WITHDRAW_COMMAND, group = Messages.GROUP_BANK_COMMANDS)
    public String recordWithdrawal(BigDecimal amount) {

        User user = bank.getUser(this.userId);
        NamedParty party = bank.getParty(user);
        Optional<TransactionAccount> account = bank.getFirstAccount(party);
        Optional<FinancialTransaction> txn = account.isPresent() ? bank.recordWithdrawal(account.get(), amount)
                : Optional.empty();

        return txn.isPresent()
                ? ConsoleWriter.write(Messages.WITHDRAWN_AMOUNT,
                        txn.get().getTransaction().getAmount().getAmount().toString())
                : ConsoleWriter.write(Messages.WITHDRAW_ERROR);
    }

    @ShellMethodAvailability("mustBeLoggedIn")
    @ShellMethod(key = "balance", value = Messages.DESC_BALANCE_COMMAND, group = Messages.GROUP_BANK_COMMANDS)
    public String checkBalance() {
        User user = bank.getUser(this.userId);
        if (user == null) {
            return ConsoleWriter.write(Messages.LOGIN_BALANCE_ERROR);
        }

        NamedParty party = bank.getParty(user);
        Optional<TransactionAccount> account = bank.getFirstAccount(party);

        return ConsoleWriter.write(Messages.USER_BALANCE, user.getUserId(),
                account.isPresent() ? account.get().getBalance().getAmount().toString() : "0");
    }

    @ShellMethodAvailability("mustBeLoggedIn")
    @ShellMethod(key = "show-transactions", value = Messages.DESC_SHOW_TRANSACTIONS_COMMAND, group = Messages.GROUP_BANK_COMMANDS)
    public String showTransactions() {
        User user = bank.getUser(this.userId);
        NamedParty party = bank.getParty(user);
        Optional<TransactionAccount> account = bank.getFirstAccount(party);

        Collection<FinancialTransaction> transactions = account.isPresent() ? bank.getAccountTransactions(account.get())
                : new ArrayList<>();

        String accountId = account.isPresent() ? account.get().getId().toString() : "0";

        return transactions.isEmpty() ? ConsoleWriter.write(Messages.NO_TRANSACTIONS_ERROR, party.getName(), accountId)
                : Printer.prettyTable(transactions, PrettyPrintFinancialTransaction.class);

    }

    @ShellMethodAvailability("mustBeLoggedIn")
    @ShellMethod(key = "logout", value = Messages.DESC_LOGOUT_COMMAND, group = Messages.GROUP_USER_COMMANDS)
    public String logout() {

        User user = bank.getUser(this.userId);
        String oldName = user != null ? user.getUserId() : null;
        clearSessionInfo();

        Printer.printBanner(environment, AltsourceApplication.class, "classpath:banner.txt");
        return ConsoleWriter.write(Messages.USER_LOGGED_OUT, oldName);

    }

    // HACK: Can't create more than one transaction account (ie Checking/Savings)
    // per user in this implementation. This only returns the first.
    @ShellMethodAvailability("accountMustExist")
    @ShellMethod(key = "select-account", value = Messages.DESC_SELECT_ACCOUNT_COMMAND, group = Messages.GROUP_ADMIN_COMMANDS)
    public String selectAccount(@ShellOption(valueProvider = AccountValueProvider.class) TransactionAccount account) {
        User user = bank.getUser(this.userId);
        NamedParty party = bank.getParty(user);
        TransactionAccount acct = bank.getPartyAccounts(party).iterator().next();

        return ConsoleWriter.write(Messages.ACCOUNT_SELECTED_BALANCE, acct.getId().toString(),
                acct.getBalance().getAmount().toString());
    }

    // convenience methods

    private void setSessionInfo(User user) {
        this.userId = user.getUserId();
    }

    private void clearSessionInfo() {
        this.userId = null;
    }

    @SuppressWarnings(value = "unused")
    private Availability alwaysAvailable() {
        return Availability.available();
    }

    @SuppressWarnings(value = "unused")
    private Availability mustBeLoggedIn() {

        return this.userId != null ? Availability.available()
                : Availability.unavailable(Messages.AVAILABILITY_NOT_LOGGED_IN);
    }

    @SuppressWarnings(value = "unused")
    private Availability accountMustExist() {
        if (this.userId == null) {
            return Availability.unavailable("Not implemented");
        }
        User user = bank.getUser(this.userId);
        NamedParty party = bank.getParty(user);
        Optional<TransactionAccount> account = bank.getFirstAccount(party);
        Collection<TransactionAccount> accounts = bank.getPartyAccounts(party);
        int accountSize = accounts.isEmpty() ? 0 : accounts.size();
        return (user != null && account.isPresent()) ? Availability.available()
                : Availability.unavailable(String.format(Messages.AVAILABILITY_MULTIPLE_ACCOUNTS, accountSize));

    }

    @SuppressWarnings(value = "unused")
    private Availability notLoggedIn() {
        return this.userId == null ? Availability.available()
                : Availability.unavailable(Messages.AVAILABILITY_ALREADY_LOGGED_IN);
    }

    @Component
    class AccountValueProvider implements ValueProvider {

        ConsoleController bsc;

        AccountValueProvider(ConsoleController bsc) {
            this.bsc = bsc;
        }

        @Override
        public boolean supports(MethodParameter parameter, CompletionContext completionContext) {
            return parameter.getParameterType().isAssignableFrom(TransactionAccount.class);
        }

        @Override
        public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext,
                String[] hints) {

            User user = bank.getUser(bsc.userId);
            NamedParty party = bank.getParty(user);
            Collection<TransactionAccount> accounts = bank.getPartyAccounts(party);

            String currentInput = completionContext.currentWordUpToCursor();
            return accounts.stream().filter(a -> a.getId().toString().contains(currentInput))
                    .map(p -> String.format("[%s] %s", p.getId(), p.getBalance().toString()))
                    .map(CompletionProposal::new).collect(Collectors.toList());

        }

    }

    @Component
    class ConnectedPromptProvider implements PromptProvider {
        private ConsoleController bsc;

        ConnectedPromptProvider(ConsoleController bsc) {
            this.bsc = bsc;
        }

        @Override
        public AttributedString getPrompt() {
            return new AttributedStringBuilder()
                    .style(AttributedStyle.BOLD_OFF.foreground(AttributedStyle.GREEN).italic()).append("GM Securities ")
                    .style(AttributedStyle.BOLD.foreground(AttributedStyle.BLACK).italic()
                            .background(AttributedStyle.MAGENTA))
                    .append("HAXX0R Console").style(AttributedStyle.BOLD_OFF.foreground(AttributedStyle.GREEN))
                    .append(String.format(" |%s>",
                            Optional.ofNullable(bsc.bank.getUser(bsc.userId)).isPresent() ? Messages.PROMPT_LOGGED_IN
                                    : Messages.PROMPT_LOGGED_OUT))
                    .toAttributedString();
        }
    }

}
