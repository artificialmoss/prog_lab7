package command;

import collectionData.Person;

import java.time.LocalDate;

/**
 * Command class
 */
public abstract class Command {
    private String name;
    private String description;
    private final Response response;

    public Command(String name, String description, Response response) {
        this.name = name;
        this.description = description;
        this.response = response;
    }

    public boolean isCallable() {
        return false;
    }

    /**
     * Executes the command
     * @param scriptMode Current script mode
     * @return Response
     */
    public abstract Response execute(boolean scriptMode);

    /**
     * @return The description of the command
     */
    public String describe() {
        return name + " — " + description;
    }

    public Command setId(Long id) {
        return this;
    }

    public Command setPerson(Person person) {
        return this;
    }

    public Command setBirthday(LocalDate birthday) {
        return this;
    }

    public Command setMode(Boolean scriptMode) {
        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns response if the command is executed successfully
     * @param message Response message
     * @return Response
     */
    public Response successfulResponse(String message) {
        return response.setSuccessfulResponse(message);
    }

    /**
     * Returns response if an error occured during command execution
     * @param message Response message
     * @return Response
     */
    public Response errorResponse(String message) {
        return response.setError(message);
    }
}
