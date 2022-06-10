package run;

import authorization.AuthManager;
import collection.CollectionManager;
import collection.DBManager;
import command.Command;
import command.CommandMapper;
import command.Response;
import command.commands.clientCommands.*;
import command.commands.serverCommands.ServerExitCommand;
import dbConfig.ConfigLoader;
import dtos.CommandRequestDTO;
import log.Log;
import org.apache.logging.log4j.Logger;
import utils.exceptions.ConnectionException;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class that manages the execution of both client and server commands
 */
public class ServerManager {
    private final CollectionManager collectionManager;
    private final Map<String, Command> clientCommands = new HashMap<>();
    private final CommandMapper commandMapper;
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final int port;
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, Command> serverCommands = new HashMap<>();
    private final Logger logger = Log.getLogger();
    private final Response response = new Response();
    private final ConfigLoader configLoader;

    private final ExecutorService readCommands = Executors.newFixedThreadPool(10);
    private final ExecutorService executeCommands = Executors.newCachedThreadPool();
    private final ExecutorService sendResponses = Executors.newCachedThreadPool();

    private final ExecuteCommand executeCommand = new ExecuteCommand();
    private final SendResponse sendResponse = new SendResponse(connectionManager);

    private final String username;
    private final String password;
    private final DBManager dbManager;
    private AuthManager authManager = null;

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
            logger.info("Received exit server command." +
                    "The application will be closed.");
            executeServerCommand(serverCommands.get("exit"));
        }
    };

    public ServerManager(String configFilePath, int port) {
        this.configLoader = new ConfigLoader(configFilePath);
        configLoader.getConfig();
        username = configLoader.getUsername();
        password = configLoader.getPassword();
        this.dbManager = new DBManager(username, password);
        dbManager.startDatabase();
        this.collectionManager = new CollectionManager(dbManager);
        this.port = port;
        commandMapper = new CommandMapper(clientCommands);
        try {
            authManager = new AuthManager(dbManager);
        } catch (NoSuchAlgorithmException e) {
            logger.fatal("Couldn't find the encryption algorithm. The application will be closed.");
            System.exit(-1);
        }
        initializeCommands();
    }

    private void initializeCommands() {
        clientCommands.put("help", new HelpCommand(clientCommands, response));
        clientCommands.put("info", new InfoCommand(collectionManager, response));
        clientCommands.put("show", new ShowCommand(collectionManager, response));
        clientCommands.put("add", new AddCommand(collectionManager, response));
        clientCommands.put("update", new UpdateByIdCommand(collectionManager, response));
        clientCommands.put("remove", new RemoveByIdCommand(collectionManager, response));
        clientCommands.put("clear", new ClearCommand(collectionManager, response));
        clientCommands.put("add_if_max", new AddIfMaxCommand(collectionManager, response));
        clientCommands.put("add_if_min", new AddIfMinCommand(collectionManager, response));
        clientCommands.put("shuffle", new ShuffleCommand(collectionManager, response));
        clientCommands.put("count_by_birthday", new CountByBirthdayCommand(collectionManager, response));
        clientCommands.put("print_birthdays", new PrintBirthdaysCommand(collectionManager, response));
        clientCommands.put("group", new GroupByHeightCommand(collectionManager, response));
        clientCommands.put("execute", new ExecuteScriptCommand(response));
        clientCommands.put("exit", new ClientExitCommand(response));
        clientCommands.put("authorize", new AuthorizeCommand(authManager, response));
        clientCommands.put("register", new RegisterCommand(authManager, response));

        serverCommands.put("exit", new ServerExitCommand(response));
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
                    Future<Command> futureCommand = readCommands.submit(this::readCommand);
                    while (!futureCommand.isDone()) {
                    }
                    Command c = futureCommand.get();
                    Future<Response> futureResponse = executeCommands.submit(executeCommand.setCommand(c));
                    while (!futureResponse.isDone()) {
                    }
                    Response res = futureResponse.get();
                    sendResponse.setResponse(res);
                    sendResponses.execute(sendResponse);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    logger.error("Couldn't connect to the client during command execution.");
                }
            }
        } catch (ConnectionException e) {
            logger.fatal("Couldn't initialize the connection on port " + port + ", try another port.");
            System.exit(0);
        }
    }

    private Command readCommand() {
        CommandRequestDTO req = connectionManager.receiveCommandRequest();
        Command c = commandMapper.getCommand(req).setClientAddress(connectionManager.getClientAddress())
                .matchUser(authManager);
        logger.info(c.getName() + " command request have been matched to a command.");
        return c;
    }

    private void executeServerCommand(Command c) {
        Response res = c.execute();
        logger.info(res.getMessage());
    }
}
