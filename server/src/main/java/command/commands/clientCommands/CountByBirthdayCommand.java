package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

import java.time.LocalDate;

/**
 * Command for counting the elements of the collection with the specified birthday
 */
public class CountByBirthdayCommand extends Command {
    private final CollectionManager collectionManager;
    private LocalDate birthday;

    public CountByBirthdayCommand(CollectionManager collectionManager, Response response) {
        super("count_by_birthday birthday",
                "count elements with the specified birthday", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute() {
        return successfulResponse(collectionManager.countBirthday(birthday));
    }

    @Override
    public Command setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }
}
