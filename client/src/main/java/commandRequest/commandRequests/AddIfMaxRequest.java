package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import collectionData.Person;
import dtos.CommandRequestDTO;
import dtos.UserDTO;
import utils.Mode;
import utils.PersonReader;

/**
 * Add element if the element is larger than the maximal element of the collection request
 */
public class AddIfMaxRequest extends CommandRequest {
    private Person person;
    private final Mode mode;
    private final PersonReader personReader;

    public AddIfMaxRequest(Mode mode, PersonReader personReader, CommandRequestDTO requestDTO, UserDTO user) {
        super("add_if_max", requestDTO, user, false);
        this.mode = mode;
        this.personReader = personReader;
    }

    @Override
    public CommandRequest setArgs(String[] input) {
        if (input.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        person = personReader.readPerson(mode, null);
        return this;
    }

    @Override
    public Person getPerson() {
        return person;
    }
}
