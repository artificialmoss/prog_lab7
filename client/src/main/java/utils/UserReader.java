package utils;

import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.UserDTO;
import utils.exceptions.NullElementException;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class UserReader implements ConsoleReader {
    private final int MAX_ERROR_COUNT;
    private final Scanner s = new Scanner(System.in);
    private final ResponseManager responseManager = new ResponseManager();

    public UserReader(int maxErrorCount) {
        this.MAX_ERROR_COUNT = maxErrorCount;
    }

    private <T> T readValue(String inputPrompt, String requirements, Function<String, T> parseValue,
                            Predicate<T> checkValue) throws WrongArgumentException, NullElementException {
        return readValue(s, responseManager, MAX_ERROR_COUNT, inputPrompt, requirements, parseValue,
                checkValue, false);
    }

    private String readUsername() {
        return readValue("Input username", "must contain only english letters and numbers",
                s -> s, s -> s.matches("^[a-zA-Z0-9]*$"));
    }

    private String readPassword() {
        return readValue("Input password", "can contain special characters, can't countain spaces",
                s -> s, s -> s.matches("\\S+"));
    }

    public UserDTO readUser() {
        return new UserDTO(readUsername(), readPassword());
    }
}
