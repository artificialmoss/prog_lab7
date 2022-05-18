package run;

import collection.CollectionManager;
import collection.JsonParser;
import collection.exceptions.CollectionException;
import command.Command;
import command.CommandMapper;
import command.Response;
import command.commands.clientCommands.*;
import command.commands.serverCommands.SaveCommand;
import command.commands.serverCommands.ServerExitCommand;
import log.Log;
import org.apache.logging.log4j.Logger;
import utils.exceptions.ConnectionException;

import java.util.*;

/**
 * Class that manages the execution of both client and server commands
 */
public class CommandManager {
    private final CollectionManager collectionManager;
    private boolean scriptMode = false;
    private final JsonParser parser;
    private final Map<String, Command> clientCommands = new HashMap<>();
    private final CommandMapper commandMapper;
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final int port;
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, Command> serverCommands = new HashMap<>();
    private final Logger logger = Log.getLogger();
    private final Response response = new Response();

    private final Runnable serverCommandExecutor = () -> {
        try {
            while (true) {
                String command = scanner.nextLine().trim().toLowerCase();
                if (serverCommands.containsKey(command)) {
                    logger.info("Received " + command + " server command.");
                    executeServerCommand(serverCommands.get(command));
                } else logger.info("The input " +
                        (command.isEmpty() ? "(empty line)" : command) + " is not a valid server command.");
            }
        } catch (NoSuchElementException e) {
            logger.info("Received save & exit server command." +
                    "The collection will be saved and the application will be closed.");
            executeServerCommand(serverCommands.get("save"));
            executeServerCommand(serverCommands.get("exit"));
        }
    };

    public CommandManager(String filepath, int port) {
        parser = new JsonParser(filepath, "saved_collection_default.json");
        this.collectionManager = parser.fileToCollection();
        this.port = port;
        initializeCommands();
        commandMapper = new CommandMapper(clientCommands);
    }

    private void initializeCommands() {
        clientCommands.put("help", new HelpCommand(clientCommands, response));
        clientCommands.put("info", new InfoCommand(collectionManager, response));
        clientCommands.put("show", new ShowCommand(collectionManager, response));
        clientCommands.put("add", new AddCommand(collectionManager, response));
        clientCommands.put("update", new UpdateByIdCommand(collectionManager, response));
        clientCommands.put("remove", new RemoveByIdCommand(collectionManager, response));
        clientCommands.put("clear", new ClearCommand(collectionManager, response));
        clientCommands.put("change_mode", new ChangeModeCommand(this, response));
        clientCommands.put("add_if_max", new AddIfMaxCommand(collectionManager, response));
        clientCommands.put("add_if_min", new AddIfMinCommand(collectionManager, response));
        clientCommands.put("shuffle", new ShuffleCommand(collectionManager, response));
        clientCommands.put("count_by_birthday", new CountByBirthdayCommand(collectionManager, response));
        clientCommands.put("print_birthdays", new PrintBirthdaysCommand(collectionManager, response));
        clientCommands.put("group", new GroupByHeightCommand(collectionManager, response));
        clientCommands.put("execute", new ExecuteScriptCommand(response));
        clientCommands.put("exit", new ClientExitCommand(response));

        serverCommands.put("save", new SaveCommand(collectionManager, parser, response));
        serverCommands.put("exit", new ServerExitCommand(response));
    }

    /**
     * Changes the script mode
     * @param scriptMode Script mode
     */
    public void setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
    }

    /**
     * Runs the process of command execution
     */
    public void run() {
        Thread thread = new Thread(serverCommandExecutor);
        thread.start();
        try {
            connectionManager.startConnection(port);
            logger.info("Server started on port " + port + ".");
            while (true) {
                try {
                    executeRequest();
                } catch (ConnectionException e) {
                    logger.error("Couldn't connect to the client during command execution.");
                }
            }
        } catch (ConnectionException e) {
            logger.fatal("Couldn't initialize the connection on port " + port + ", try another port.");
            System.exit(0);
        }
    }

    private void executeRequest() {
        try {
            Command c = commandMapper.getCommand(connectionManager.receiveCommandRequest());
            logger.info(c.getName() + " command request has been matched to a command.");
            if (c != null) {
                Response response = c.execute(scriptMode);
                logger.info(c.getName() + " command has been handled.");
                logger.trace("Response message:\n" + response.getMessage());
                connectionManager.sendResponse(response);
            }
        } catch (CollectionException e) {
            Response response = new Response().setError(e.getMessage());
            connectionManager.sendResponse(response);
        }
    }

    private void executeServerCommand(Command c) {
        Response res = c.execute(true);
        logger.info(res.getMessage());
    }
}
