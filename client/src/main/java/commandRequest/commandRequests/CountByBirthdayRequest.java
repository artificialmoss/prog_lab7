package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;

import java.time.LocalDate;
import java.time.Month;

/**
 * Count the number of elements with specified birthdays request
 */
public class CountByBirthdayRequest extends CommandRequest {
    private LocalDate birthday;

    public CountByBirthdayRequest(CommandRequestDTO requestDTO) {
        super("count_by_birthday", requestDTO);
    }

    @Override
    public CommandRequest setArgs(String[] input) {
        if (input.length == 4) {
            try {
                int day = Integer.parseInt(input[1]);
                int month = Integer.parseInt(input[2]);
                int year = Integer.parseInt(input[3]);
                if ((year < LocalDate.MIN.getYear() || year > LocalDate.MAX.getYear()) ||
                        (month < 1 || month > 12)) {
                    throw new WrongArgumentException();
                }
                int maxDay = Month.of(month).length(LocalDate.of(year, month, 1).isLeapYear());
                if (day < 1 || day > maxDay) {
                    throw new WrongArgumentException();
                }
                birthday = LocalDate.of(year, month, day);
            } catch (NumberFormatException e) {
                throw new WrongArgumentException();
            }
        } else {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }

    @Override
    public LocalDate getBirthday() {
        return birthday;
    }
}
