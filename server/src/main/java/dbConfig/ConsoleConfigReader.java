package dbConfig;

import dbConfig.exceptions.NoUserInfoException;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConsoleConfigReader {
    private final int MAX_ERROR_COUNT;
    private final Scanner s = new Scanner(System.in);

    public ConsoleConfigReader(int maxErrorCount) {
        MAX_ERROR_COUNT = maxErrorCount;
    }

    private <T> T readValue(String inputPrompt, String requirements, Supplier<String> supplier,
                            Function<String, T> parseValue, Predicate<T> checkValue) {
        int errorCount = 0;
        T res;
        System.out.println(inputPrompt + " (" + requirements + "): ");
        while (errorCount != MAX_ERROR_COUNT) {
            String input = supplier.get();
            try {
                res = parseValue.apply(input);
                if (checkValue.test(res)) {
                    return res;
                } else {
                    errorCount++;
                    if (errorCount != MAX_ERROR_COUNT)
                        System.out.println("Invalid argument (" + requirements + "), try again: ");
                }
            } catch (IllegalArgumentException e) {
                errorCount++;
                if (errorCount != MAX_ERROR_COUNT)
                    System.out.println("Invalid argument (" + requirements + "), try again: ");
            }
        }
        throw new NoUserInfoException();
    }

    public String readUsername() {
        return readValue("Input username", "must contain only english letters and numbers",
                () -> s.nextLine().trim(), x -> x, x -> x.matches("^[a-zA-Z0-9]*$"));
    }

    //fixme change to readpassword (also on client!)
    // - (at the last moment because System.console() is not accessible in IDEA
    public String readPassword() {
        //return readValue("Input password", "can contain special characters, can't countain spaces",
                //() -> new String(System.console().readPassword()), s -> s, s -> s.matches("\\S+"));
        return readValue("Input password", "can contain special characters, can't countain spaces",
                () -> s.nextLine().trim(), s -> s, s -> s.matches("\\S+"));
    }
}
