package com.dhcdata.altsource.controller.shell;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import com.dhcdata.altsource.controller.shell.provider.ConsoleWriter;
import com.dhcdata.altsource.view.util.PrettyClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ResourceBanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

public class Printer {
    private static Logger log = LoggerFactory.getLogger(Printer.class);

    private Printer() {
    }

    public static void printBanner(Environment environment, Class<?> application, String bannerLocation) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(bannerLocation);
        ResourceBanner b = new ResourceBanner(resource);
        b.printBanner(environment, application, System.out);
    }

    public static <T, U extends PrettyClass> String prettyTable(Collection<T> rows, Class<U> prettyClass) {
        int[] idx = { 0 };
        String[][] data = new String[rows.size() + 1][prettyClass.getDeclaredFields().length];
        try {
            data[idx[0]++] = (String[]) prettyClass.getDeclaredMethod("getHeader").invoke((Object) null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            log.error("{}", e);

        }
        rows.forEach(it -> {
            try {
                data[idx[0]++] = prettyClass
                        .getDeclaredConstructor(
                                (Class<?>) prettyClass.getDeclaredMethod("getType").invoke((Object) null))
                        .newInstance(it).toStringArray();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
                log.error("{}", e1);
            }
        });

        return new TableBuilder(new ArrayTableModel(data)).addFullBorder(BorderStyle.fancy_double).build().render(80);

    }
}
