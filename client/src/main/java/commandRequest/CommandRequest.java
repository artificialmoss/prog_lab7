package commandRequest;


import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import collectionData.Person;
import dtos.CommandRequestDTO;

import java.time.LocalDate;

/**
 * Abstract command request class
 */
public abstract class CommandRequest {
    private final String name;
    private final CommandRequestDTO requestDTO; //= new CommandRequestDTO();

    /**
     * @param name Command request name
     */
    public CommandRequest(String name, CommandRequestDTO requestDTO) {
        this.name = name;
        this.requestDTO = requestDTO;
    }

    /**
     * Sets argumentss from input line
     * @param s Input
     * @return Command request with arguments
     * @throws WrongAmountOfArgumentsException Thrown when encountering unexpected number of arguments
     * @throws WrongArgumentException Thrown when arguments don't fit the requirements
     */
    public abstract CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException,
            WrongArgumentException;

    public LocalDate getBirthday() {
        return null;
    }

    public Long getId() {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    public Boolean getScriptMode() {
        return null;
    }

    /**
     * Gets command data object transfer to be sent to the server
     * @return Resulting DTO
     */
    public CommandRequestDTO getDTO() {
        requestDTO.setName(name);
        requestDTO.setScriptMode(this.getScriptMode());
        requestDTO.setBirthday(this.getBirthday());
        requestDTO.setId(this.getId());
        requestDTO.setPerson(this.getPerson());
        return requestDTO;
    }
}
