package utils;

import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.UserDTO;
import utils.exceptions.NullElementException;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class UserReader implements ConsoleReader {
    private final int MAX_ERROR_COUNT;
    private final Scanner s = new Scanner(System.in);
    private final ResponseManager responseManager = new ResponseManager();

    public UserReader(int maxErrorCount) {
        this.MAX_ERROR_COUNT = maxErrorCount;
    }

    private <T> T readValue(String inputPrompt, String requirements, Supplier<String> supplier,
                            Function<String, T> parseValue, Predicate<T> checkValue)
            throws WrongArgumentException, NullElementException {
        return readValue(responseManager, MAX_ERROR_COUNT, inputPrompt, requirements, supplier,
                parseValue, checkValue, false);
    }

    private String readUsername() {
        return readValue("Input username", "must contain only english letters and numbers",
                () -> s.nextLine().trim(), s -> s, s -> s.matches("^[a-zA-Z0-9]*$"));
    }

    //fixme
    private String readPassword() {
        /*
        return readValue("Input password", "can contain special characters, can't countain spaces",
                () -> new String(System.console().readPassword()), s -> s, s -> s.matches("\\S+"), false);
         */
        return readValue("Input password", "can contain special characters, can't countain spaces",
                () -> s.nextLine().trim(), s -> s, s -> s.matches("\\S+"));
    }

    public UserDTO readUser() {
        return new UserDTO(readUsername(), readPassword());
    }
}
