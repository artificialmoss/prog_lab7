package command;

import authorization.AuthManager;
import collectionData.Person;
import dtos.UserDTO;

import java.net.SocketAddress;
import java.time.LocalDate;

/**
 * Command class
 */
public abstract class Command {
    private String name;
    private String description;
    private final Response response;
    private final boolean isCallableFromClientConsole;

    public Command(String name, String description, Response response) {
        this.name = name;
        this.description = description;
        this.response = response;
        isCallableFromClientConsole = true;
    }

    public Command(String name, Response response) {
        this.name = name;
        this.response = response;
        isCallableFromClientConsole = false;
    }

    public boolean isCallableFromClientConsole() {
        return isCallableFromClientConsole;
    }

    /**
     * Executes the command
     * @return Response
     */
    public abstract Response execute();

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

    public Command setUserDTO(UserDTO user) { return this; }

    public Command matchUser(AuthManager authManager) {
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

    public Command setClientAddress(SocketAddress clientAddress) {
        this.response.setClientAddress(clientAddress);
        return this;
    }

    public SocketAddress getClientAddress() {
        return response.getClientAddress();
    }
}
