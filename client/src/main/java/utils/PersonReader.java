package utils;

import commandRequest.commandRequests.exceptions.WrongArgumentException;
import collectionData.*;
import utils.exceptions.NullElementException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class for reading elements of the collection
 */
public class PersonReader implements ConsoleReader {
    private final int MAX_ERROR_COUNT;
    private Scanner s;
    private final ResponseManager responseManager = new ResponseManager();

    /**
     * Constructor
     * @param maxErrorCount The maximal number of mistakes for each field allowed
     */
    public PersonReader(int maxErrorCount) {
        this.MAX_ERROR_COUNT = maxErrorCount;
    }

    private <T> T readValue(String inputPrompt, String requirements, Function<String, T> parseValue, Predicate<T> checkValue,
                            boolean nullAllowed) throws WrongArgumentException, NullElementException {
        return readValue(s, responseManager, MAX_ERROR_COUNT, inputPrompt, requirements, parseValue,
                checkValue, nullAllowed);
    }

    private String readName() {
        String inputPrompt = "Input their name";
        String requirements = "must not be empty";
        return readValue(inputPrompt, requirements, s -> s, s -> !s.isEmpty(), false);
    }

    private Coordinates readCoordinates() {
        String xInputPrompt = "Input the first coordinate";
        String xRequirements = "must be a decimal, max value = 212.0";
        double x = readValue(xInputPrompt, xRequirements, Double::parseDouble, s -> s <= 212, false);
        String yInputPrompt = "Input the second coordinate";
        String yRequirements = "must be a decimal";
        Float y = readValue(yInputPrompt, yRequirements, Float::parseFloat, s -> true, false);
        return new Coordinates(x, y);
    }

    private Long readHeight() {
        String inputPrompt = "Type height";
        String requirements = "must be a positive long value (integer lower than or equal to " + Long.MAX_VALUE + ")";
        return readValue(inputPrompt, requirements, Long::parseLong, s -> s > 0, false);
    }

    private LocalDateTime readBirthday() throws WrongArgumentException {
        try {
            String yearInputPrompt = "Type the year of their birth";
            String yearRequirements = "must be an integer greater than " + LocalDateTime.MIN.getYear()
                    + " and less than " + LocalDateTime.MAX.getYear()
                    + "; input an empty line if you don't want to specify this element's birthday";
            int year = readValue(yearInputPrompt, yearRequirements, Integer::parseInt,
                    s -> (s >= LocalDateTime.MIN.getYear()) && (s <= LocalDateTime.MAX.getYear()), true);
            String monthInputPrompt = "Type the month of their birth";
            String monthRequirements = "must be an integer between 1 and 12"
                    + "; input an empty line if you don't want to specify this element's birthday";
            Month month = Month.of(readValue(monthInputPrompt, monthRequirements, Integer::parseInt,
                    s -> (s >= 1) && (s <= 12), true));
            final int maxDay = month.length(LocalDate.of(year, month, 1).isLeapYear());
            String dayInputPrompt = "Type the day of their birth";
            String dayRequirements = "must be an integer between 1 and " + maxDay
                    + "; input an empty line if you don't want to specify this element's birthday";
            int day = readValue(dayInputPrompt, dayRequirements, Integer::parseInt,
                    s -> (s >= 1) && (s <= maxDay), true);
            String hourInputPrompt = "Type the hour of their birth";
            String hourRequirements = "must be an integer between 0 and 23, if unsure, type 0"
                    + "; input an empty line if you don't want to specify this element's birthday";
            int hour = readValue(hourInputPrompt, hourRequirements, Integer::parseInt,
                    s -> (s >= 0) && (s <= 23), true);
            String minuteInputPrompt = "Type the minute of their birth";
            String minuteRequirements = "must be an integer between 0 and 59, if unsure, type 0"
                    + "; input an empty line if you don't want to specify this element's birthday";
            int minute = readValue(minuteInputPrompt, minuteRequirements, Integer::parseInt,
                    s -> (s >= 0) && (s <= 59), true);
            return LocalDateTime.of(year, month, day, hour, minute);
        } catch (NullElementException e) {
            return null;
        }
    }

    private Color readColor() {
        String colorInputPrompt = "Type their hair color";
        String colorRequirements = "must be one of the following: RED, BLACK, YELLOW, WHITE, BROWN";
        return readValue(colorInputPrompt, colorRequirements, s -> Color.valueOf(s.toUpperCase()), s -> true, false);
    }

    private Country readCountry() {
        try {
            String countryInputPrompt = "Type their nationality";
            String countryRequirements = "must be one of the following: RUSSIA, SPAIN, VATICAN, ITALY; " +
                    "input an empty line if you don't want to specify this element's nationality";
            return readValue(countryInputPrompt, countryRequirements, s -> Country.valueOf(s.toUpperCase()), s -> true, true);
        } catch (NullElementException e) {
            return null;
        }
    }

    private Location readLocation() {
        String xInputPrompt = "Location input: type the first coordinate";
        String xRequirements = "must be an integer between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE;
        int x = readValue(xInputPrompt, xRequirements, Integer::parseInt, s -> true, false);
        String yInputPrompt = "Type the second coordinate";
        String yRequirements = "must be a decimal";
        Double y = readValue(yInputPrompt, yRequirements, Double::parseDouble, s -> true, false);
        responseManager.showMessage("Type their location name (can be blank): ");
        String name = s.nextLine();
        return new Location(x, y, name);
    }

    public Person readPersonFromConsole(Mode mode, Long id) throws WrongArgumentException {
        s = mode.getScanner();
        try {
            responseManager.showResponse("You've got " + MAX_ERROR_COUNT + " tries for each input parameter, if you fail all of them, you will need to call your command again. " +
                    "Some parameters (birthday, nationality) can be unspecified, the location name can be blank.");
            String name = readName();
            Coordinates coordinates = readCoordinates();
            Long height = readHeight();
            LocalDateTime birthday = readBirthday();
            Color hairColor = readColor();
            Country nationality = readCountry();
            Location location = readLocation();
            return new Person(id, name, coordinates, height, birthday, hairColor, nationality, location);
        } catch (NoSuchElementException e) {
            throw new WrongArgumentException();
        }
    }

    private <T> T readValueFromScript(Function<String, T> parseValue, Predicate<T> checkValue, boolean nullAllowed)
            throws WrongArgumentException, NullElementException {
        T res;
        String input = s.nextLine().trim();
        if (input.isEmpty() && nullAllowed) {
            throw new NullElementException();
        }
        try {
            res = parseValue.apply(input);
            if (checkValue.test(res)) {
                return res;
            }
            throw new WrongArgumentException();
        } catch (IllegalArgumentException e) {
            throw new WrongArgumentException();
        }
    }

    private String readNameFromScript() {
        return readValueFromScript(s -> s, s -> !s.isEmpty(), false);
    }

    private Coordinates readCoordinatesFromScript() {
        double x = readValueFromScript(Double::parseDouble, s -> s <= 212, false);
        Float y = readValueFromScript(Float::parseFloat, s -> true, false);
        return new Coordinates(x, y);
    }

    private Long readHeightFromScript() {
        return readValueFromScript(Long::parseLong, s -> s > 0, false);
    }

    private LocalDateTime readBirthdayFromScript() throws WrongArgumentException {
        try {
            int year = readValueFromScript(Integer::parseInt,
                    s -> (s >= LocalDateTime.MIN.getYear()) && (s <= LocalDateTime.MAX.getYear()), true);
            Month month = Month.of(readValueFromScript(Integer::parseInt,
                    s -> (s >= 1) && (s <= 12), true));
            final int maxDay = month.length(LocalDate.of(year, month, 1).isLeapYear());
            int day = readValueFromScript(Integer::parseInt,
                    s -> (s >= 1) && (s <= maxDay), true);
            int hour = readValueFromScript(Integer::parseInt,
                    s -> (s >= 0) && (s <= 23), true);
            int minute = readValueFromScript(Integer::parseInt,
                    s -> (s >= 0) && (s <= 59), true);
            return LocalDateTime.of(year, month, day, hour, minute);
        } catch (NullElementException e) {
            return null;
        }
    }

    private Color readColorFromScript() {
        return readValueFromScript(s -> Color.valueOf(s.toUpperCase()), s -> true, false);
    }

    private Country readCountryFromScript() {
        try {
            return readValueFromScript(s -> Country.valueOf(s.toUpperCase()), s -> true, true);
        } catch (NullElementException e) {
            return null;
        }
    }

    private Location readLocationFromScript() {
        int x = readValueFromScript(Integer::parseInt, s -> true, false);
        Double y = readValueFromScript(Double::parseDouble, s -> true, false);
        String name = s.nextLine();
        return new Location(x, y, name);
    }

    public Person readPersonFromScript(Mode mode, Long id) throws WrongArgumentException {
        s = mode.getScanner();
        String name = readNameFromScript();
        Coordinates coordinates = readCoordinatesFromScript();
        Long height = readHeightFromScript();
        LocalDateTime birthday = readBirthdayFromScript();
        Color hairColor = readColorFromScript();
        Country nationality = readCountryFromScript();
        Location location = readLocationFromScript();
        return new Person(id, name, coordinates, height, birthday, hairColor, nationality, location);
    }

    public Person readPerson(Mode mode, Long id) {
        if (mode.getScriptMode()) {
            return readPersonFromScript(mode, id);
        }
        return readPersonFromConsole(mode, id);
    }
}
