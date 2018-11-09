package com.dhcdata.altsource.constants;

public class Messages {

    public static final String USER_EXISTS_ERROR = "User name: %s already exists";
    public static final String ACCOUNT_CREATED = "Account Created.\n%s";
    public static final String LOGIN_COMMAND_DESC = "Login to the Bank. \n\t\tUsage: login name password";
    public static final String CONNECTED_USER = "connected %s";
    public static final String CREDENTIALS_ERROR = "Bad userName or password %s / %s. Please try again";
    public static final String DEPOSIT_AMOUNT = "You have deposited $%s";
    public static final String WITHDRAWN_AMOUNT = "You have withdrawn $%s";
    public static final String DEPOSIT_ERROR = "Deposit failed.";
    public static final String WITHDRAW_ERROR = "Withdrawal failed";
    public static final String USER_BALANCE = "%s's balance = $%s.";
    public static final String LOGIN_BALANCE_ERROR = "User is not logged in. Cannot check balance.";
    public static final String NO_TRANSACTIONS_ERROR = "Sorry, no transactions for %s [%s]";
    public static final String USER_LOGGED_OUT = "%s has logged out.";
    public static final String NO_ADMIN_TRANSACTIONS_ERROR = "Sorry, no transactions for anyone";
    public static final String ACCOUNT_SELECTED_BALANCE = "Account #%s selected with a balance of $%s";

    public static final String DESC_CREATE_ACCOUNT_COMMAND = "Create account. \n\t\tUsage: create-account userName password first-name last-name 00.00";
    public static final String DESC_DEPOSIT_COMMAND = "Deposit funds \n\t\tUsage: deposit 0.00";
    public static final String DESC_WITHDRAW_COMMAND = "Withdraw funds \n\t\tUsage: withdraw 0.00";
    public static final String DESC_BALANCE_COMMAND = "Check balance";
    public static final String DESC_SHOW_TRANSACTIONS_COMMAND = "List all transactions for user";
    public static final String DESC_LOGOUT_COMMAND = "Logout of the Bank";
    public static final String DESC_SELECT_ACCOUNT_COMMAND = "Choose your account.";
    public static final String DESC_SHOW_ALL_TRANSACTIONS_COMMAND = "List all transactions";

    public static final String GROUP_USER_COMMANDS = "User Commands";
    public static final String GROUP_BANK_COMMANDS = "Bank Commands";
    public static final String GROUP_ADMIN_COMMANDS = "Admin Test Commands";

    public static final String AVAILABILITY_NOT_LOGGED_IN = "you are not logged in.";
    public static final String AVAILABILITY_MULTIPLE_ACCOUNTS = "you have %s accounts available.";
    public static final String AVAILABILITY_ALREADY_LOGGED_IN = "You are already logged in.";

    public static final String PROMPT_LOGGED_IN = "Logged In";
    public static final String PROMPT_LOGGED_OUT = "Logged Out";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_HIGHLIGHT = "\u001B[33m";

    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAW = "WITHDRAW";
    public static final String INITIAL = "INITIAL";

    private Messages() {
    }

}