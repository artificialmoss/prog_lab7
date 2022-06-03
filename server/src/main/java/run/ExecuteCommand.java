package run;

import collection.exceptions.CollectionException;
import command.Command;
import command.Response;
import log.Log;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class ExecuteCommand implements Callable<Response> {
    private Command command;
    private final Logger logger = Log.getLogger();

    public ExecuteCommand setCommand(Command command) {
        this.command = command;
        return this;
    }

    @Override
    public Response call() throws Exception {
        Response response;
        try {
            response = command.execute();
        } catch (CollectionException e) {
            response = new Response().setClientAddress(command.getClientAddress()).setError(e.getMessage());
        }
        logger.info(command.getName() + " command has been handled.");
        logger.trace("Response message:\n" + response.getMessage());
        return response;
    }
}
