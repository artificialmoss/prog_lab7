package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import collectionData.Person;
import dtos.CommandRequestDTO;
import utils.Mode;
import utils.PersonReader;

/**
 * Add element request
 */
public class AddRequest extends CommandRequest {
    private final Mode mode;
    private Person person;
    private final PersonReader personReader;

    public AddRequest(Mode mode, PersonReader personReader, CommandRequestDTO requestDTO) {
        super("add", requestDTO);
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
