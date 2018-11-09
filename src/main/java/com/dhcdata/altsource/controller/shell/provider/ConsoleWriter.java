package com.dhcdata.altsource.controller.shell.provider;

import com.dhcdata.altsource.constants.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.slf4j.

public class ConsoleWriter {
    private static Logger log = LoggerFactory.getLogger(ConsoleWriter.class);

    private ConsoleWriter() {
    }

    public static String write(String msg, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(">");
        sb.append(Messages.ANSI_HIGHLIGHT);
        sb.append(String.format(msg, (Object[]) args));
        sb.append(Messages.ANSI_RESET);
        return sb.toString();

    }

    public static void writeNow(String msg, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(">");
        sb.append(Messages.ANSI_HIGHLIGHT);
        sb.append(String.format(msg, (Object[]) args));
        sb.append(Messages.ANSI_RESET);
        log.info("{}", sb);

    }
}
