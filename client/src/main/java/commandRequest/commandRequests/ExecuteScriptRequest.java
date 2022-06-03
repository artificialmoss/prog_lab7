package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.*;
import dtos.CommandRequestDTO;
import dtos.UserDTO;
import utils.RequestManager;
import utils.ResponseManager;
import utils.exceptions.ConnectionException;
import utils.exceptions.RecursiveScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Execute script request
 */
public class ExecuteScriptRequest extends CommandRequest implements Runnable {
    private File file;
    private final RequestManager requestManager;
    private final ResponseManager responseManager;

    public ExecuteScriptRequest(RequestManager requestManager, ResponseManager responseManager,
                                CommandRequestDTO requestDTO, UserDTO user) {
        super("execute", requestDTO, user, false);
        this.requestManager = requestManager;
        this.responseManager = responseManager;
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 2) {
            throw new WrongAmountOfArgumentsException();
        }
        String filepath = s[1];
        Path path = Paths.get(filepath);
        if (!Files.exists(path) | Files.isDirectory(path) | !Files.isReadable(path)) {
            throw new NoScriptException("This script doesn't exist or can't be accessed.");
        }
        file = new File(filepath);
        return this;
    }

    public void run() {
        Scanner curScanner = requestManager.getScanner();
        try {
            requestManager.addScript(file);
            System.out.println("filename — " + file.getName());
            requestManager.run();
            file = new File(requestManager.peekLast());
            requestManager.removeScript(curScanner);
        } catch (RecursiveScriptException e) {
            file = new File(requestManager.peekFirst());
            requestManager.clearScriptHistory();
            responseManager.showResponse(file.getAbsolutePath() + " contains recursion and can't be executed. " +
                    "All commands before the recursive call have been executed.");
            return;
        } catch (ConnectionException e) {
            requestManager.clearScriptHistory();
            responseManager.showResponse("Error — lost connection to the server during script execution.");
        }
        catch (FileNotFoundException e) {
            if (requestManager.getScriptMode()) throw new ScriptErrorException();
            responseManager.showResponse("File not found.", !requestManager.getScriptMode());
            return;
        } catch (ScriptErrorException e) {
            file = new File(requestManager.peekLast());
            requestManager.removeScript(curScanner);
            if (requestManager.getScriptMode()) throw new ScriptErrorException();
            responseManager.showResponse(file.getAbsolutePath() + " contains a mistake. " +
                    "All commands before the mistake have been executed.", !requestManager.getScriptMode());
            return;
        } catch (ScriptExecutionException e) {
            file = new File(requestManager.peekFirst());
            requestManager.clearScriptHistory();
            responseManager.showResponse("One of the commands in the script cannot be executed. " +
                    "Server response: " + e.getMessage() + "\nAll commands before it have been executed.");
        }
        responseManager.showResponse(file.getAbsolutePath() + " has been successfully executed.",
                !requestManager.getScriptMode());
    }
}
