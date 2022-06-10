package utils;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import commandRequest.commandRequests.*;
import commandRequest.commandRequests.exceptions.NoScriptException;
import dtos.CommandRequestDTO;
import dtos.ResponseDTO;
import dtos.UserDTO;
import responseData.Status;
import utils.exceptions.ConnectionException;
import utils.exceptions.NoRequestException;
import utils.exceptions.RecursiveScriptException;
import utils.exceptions.WrongRequestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Class that runs the process of request execution
 */
public class RequestManager {
    private final Deque<String> scriptHistory = new ArrayDeque<>();
    private final Mode mode = new Mode();
    private final Map<String, CommandRequest> commandRequests = new HashMap<>();
    private final ResponseManager responseManager = new ResponseManager();
    private final PersonReader personReader = new PersonReader(3);
    private final UserReader userReader = new UserReader(3);
    private final ConnectionManager connectionManager;
    private final CommandRequestDTO requestDTO = new CommandRequestDTO();
    private UserDTO user = null;

    public RequestManager(int port, int TIMEOUT) {
        connectionManager = new ConnectionManager(port, TIMEOUT);
    }

    private void authorize() {
        Map<String, CommandRequest> authorizationRequests = new HashMap<>();
        authorizationRequests.put("authorize", new AuthorizeRequest(userReader, requestDTO));
        authorizationRequests.put("register", new RegisterRequest(userReader, requestDTO));
        authorizationRequests.put("exit", new ExitRequest(responseManager, requestDTO));

        try {
            responseManager.showResponse("You need to be logged in to use this application.");
            while (true) {
                responseManager.showResponse("Type \"authorize\" to authorize, type \"register\" to register, " +
                        "type \"exit\" to exit the application.");
                responseManager.showMessage("$ ");
                String input = mode.getScanner().nextLine().trim().toLowerCase();
                if (authorizationRequests.containsKey(input)) {
                    CommandRequest authRequest = authorizationRequests.get(input).setArgs();
                    ResponseDTO responseDTO = handleRequest(authRequest);
                    responseManager.showResponse(responseDTO.getMessage());
                    if (responseDTO.getStatus() == Status.OK) {
                        this.user = authRequest.getUser();
                        break;
                    }
                } else {
                    responseManager.showResponse("Invalid command.");
                }
            }
        } catch (NoSuchElementException e) {
            responseManager.showResponse("You have entered the end of file symbol. The application will be closed.");
            System.exit(0);
        }
    }

    private void initializeCommandRequests() {
        commandRequests.put("add", new AddRequest(mode, personReader, requestDTO, user));
        commandRequests.put("add_if_max", new AddIfMaxRequest(mode, personReader, requestDTO, user));
        commandRequests.put("add_if_min", new AddIfMinRequest(mode, personReader, requestDTO, user));
        commandRequests.put("clear", new ClearRequest(requestDTO, user));
        commandRequests.put("count_by_birthday", new CountByBirthdayRequest(requestDTO));
        commandRequests.put("exit", new ExitRequest(responseManager, requestDTO));
        commandRequests.put("group", new GroupByHeightRequest(requestDTO));
        commandRequests.put("help", new HelpRequest(requestDTO));
        commandRequests.put("info", new InfoRequest(requestDTO));
        commandRequests.put("print_birthdays", new PrintBirthdaysRequest(requestDTO));
        commandRequests.put("remove", new RemoveByIdRequest(requestDTO, user));
        commandRequests.put("show", new ShowRequest(requestDTO));
        commandRequests.put("shuffle", new ShuffleRequest(requestDTO));
        commandRequests.put("update", new UpdateByIdRequest(mode, personReader, requestDTO, user));
        commandRequests.put("execute", new ExecuteScriptRequest(this, responseManager, requestDTO, user));
    }

    public boolean getScriptMode() {
        return mode.getScriptMode();
    }

    public Scanner getScanner() {
        return mode.getScanner();
    }

    private CommandRequest matchCommandRequest(String[] s) throws NoRequestException, WrongRequestException {
        if (s.length == 0) {
            throw new NoRequestException();
        }
        String requestName = s[0].trim().toLowerCase();
        if (requestName.isEmpty()) throw new NoRequestException();
        if (commandRequests.containsKey(requestName)) {
            return commandRequests.get(requestName).setArgs(s);
        } else throw new WrongRequestException();
    }

    private CommandRequest getCommandRequest(String[] s) {
        try {
            return matchCommandRequest(s);
        } catch (NoScriptException e) {
            responseManager.showErrorMessage("This script doesn't exist or can't be accessed.",
                    !getScriptMode(), true);
        } catch (NoRequestException e) {
            responseManager.showErrorMessage("No command, try again.",
                    !getScriptMode(), false);
        } catch (WrongRequestException e) {
            responseManager.showErrorMessage("No such command, try again.",
                    !getScriptMode(), true);
        } catch (WrongAmountOfArgumentsException | WrongArgumentException e) {
            responseManager.showErrorMessage("Wrong arguments, try again.",
                    !getScriptMode(), true);
        }
        return null;
    }

    /**
     * Methods that either runs the command request if it's runnable or sends it to the server and gets
     * the response
     * @param c Command request
     * @return Response
     */
    public ResponseDTO handleRequest(CommandRequest c) {
        if (c instanceof Runnable) {
            ((Runnable) c).run();
            return new ResponseDTO();
        }
        return connectionManager.handleRequest(c, getScriptMode());
    }

    public void start() {
        try {
            connectionManager.start();
            authorize();
            initializeCommandRequests();
            responseManager.showResponse("Enter \"help\" to see available commands. " +
                    "Enter \"exit\" or CTRL + D to close the application", !getScriptMode());
        } catch (ConnectionException e) {
            if (getScriptMode()) {
                throw new ConnectionException();
            }
            responseManager.showResponse("Couldn't connect to the server. The application will be closed.");
            System.exit(0);
        }
    }

    /**
     * Runs the process of request reading and execution
     */
    public void run() {
        while (true) {
            responseManager.showMessage("$ ", !getScriptMode());
            if (getScanner().hasNextLine()) {
                String s = getScanner().nextLine().trim();
                String[] input = s.split("\\s+");
                CommandRequest commandRequest = getCommandRequest(input);
                if (commandRequest != null) {
                    responseManager.showResponse(handleRequest(commandRequest).getMessage(),
                            !getScriptMode() || commandRequest.showResponseInScriptMode());
                }
            } else {
                if (!getScriptMode()) {
                    responseManager.showResponse("\nThe application will be closed.");
                    handleRequest(new ExitRequest(responseManager, requestDTO));
                }
                return;
            }
        }
    }

    /**
     * Adds a script to the script history and changes the scanner used for request reading
     * @param file The script file
     * @throws FileNotFoundException
     */
    public void addScript(File file) throws FileNotFoundException {
        try {
            String canonicalPath = file.getCanonicalPath();
            if (scriptHistory.contains(canonicalPath)) {
                throw new RecursiveScriptException();
            }
            mode.setScriptMode(true);
            mode.setScanner(new Scanner(file));
            scriptHistory.push(canonicalPath);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }

    /**
     * Removes the script from script history and changes the scanner used for request reading
     * @param prevScanner The previous scanner
     */
    public void removeScript(Scanner prevScanner) {
        scriptHistory.pop();
        if (scriptHistory.isEmpty()) {
            mode.setScriptMode(false);
        }
        if (prevScanner != null) {
            mode.setScanner(prevScanner);
        } else {
            throw new NullPointerException("Unknown error: no scanner.");
        }
    }

    /**
     * Returns the oldest element of the script history
     * @return The oldest element of the script history
     */
    public String peekFirst() {
        return scriptHistory.peekFirst();
    }

    /**
     * Returns the newest element of the script history
     * @return The oldest element of the script history
     */
    public String peekLast() {
        return scriptHistory.peekLast();
    }

    /**
     * Clears the script history
     */
    public void clearScriptHistory() {
        scriptHistory.clear();
        mode.setScriptMode(false);
        mode.setScanner(new Scanner(System.in));
    }
}
