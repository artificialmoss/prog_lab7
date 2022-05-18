package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import collectionData.Person;
import dtos.CommandRequestDTO;
import utils.Mode;
import utils.PersonReader;

/**
 * Update element with the specified id request
 */
public class UpdateByIdRequest extends CommandRequest {
    private Long id;
    private Person person;
    private final Mode mode;
    private final PersonReader personReader;

    public UpdateByIdRequest(Mode mode, PersonReader personReader, CommandRequestDTO requestDTO) {
        super("update", requestDTO);
        this.mode = mode;
        this.personReader = personReader;
    }

    @Override
    public CommandRequest setArgs(String[] input) {
        if (input.length != 2) {
            throw new WrongAmountOfArgumentsException();
        }
        try {
            id = Long.parseLong(input[1]);
            person = personReader.readPerson(mode, id);
        } catch (NumberFormatException e) {
            throw new WrongArgumentException();
        }
        return this;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public Long getId() {
        return id;
    }
}
