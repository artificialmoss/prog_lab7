package configuration;

import configuration.exceptions.NoUserInfoException;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConsoleConfigReader {
    private final int MAX_ERROR_COUNT;
    private final Scanner s = new Scanner(System.in);

    public ConsoleConfigReader(int maxErrorCount) {
        MAX_ERROR_COUNT = maxErrorCount;
    }

    private <T> T readValue(String inputPrompt, String requirements, Function<String, T> parseValue,
                            Predicate<T> checkValue) {
        int errorCount = 0;
        T res;
        System.out.println(inputPrompt + " (" + requirements + "): ");
        while (errorCount != MAX_ERROR_COUNT) {
            String input = s.nextLine().trim();
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
                s -> s, s -> s.matches("^[a-zA-Z0-9]*$"));
    }

    public String readPassword() {
        return readValue("Input password", "can contain special characters, can't countain spaces",
                s -> s, s -> s.matches("\\S+"));
    }
}
