package utils;

import commandRequest.commandRequests.exceptions.WrongArgumentException;
import utils.exceptions.NullElementException;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ConsoleReader {

    default <T> T readValue(ResponseManager responseManager, int MAX_ERROR_COUNT,
                    String inputPrompt, String requirements, Supplier<String> supplier,
                    Function<String, T> parseValue, Predicate<T> checkValue,
                    boolean nullAllowed) throws WrongArgumentException, NullElementException {
        int errorCount = 0;
        T res;
        responseManager.showMessage(inputPrompt + " (" + requirements + "): ");
        while (errorCount != MAX_ERROR_COUNT) {
            String input = supplier.get();
            if (input.isEmpty() && nullAllowed) {
                throw new NullElementException();
            }
            try {
                res = parseValue.apply(input);
                if (checkValue.test(res)) {
                    return res;
                } else {
                    errorCount++;
                    if (errorCount != MAX_ERROR_COUNT)
                        responseManager.showMessage("Invalid argument (" + requirements + "), try again: ");
                }
            } catch (IllegalArgumentException e) {
                errorCount++;
                if (errorCount != MAX_ERROR_COUNT)
                    responseManager.showMessage("Invalid argument (" + requirements + "), try again: ");
            }
        }
        throw new WrongArgumentException();
    }
}
