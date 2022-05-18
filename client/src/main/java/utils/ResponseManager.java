package utils;

import commandRequest.commandRequests.exceptions.ScriptErrorException;

/**
 * Class that shows or sends various responses
 */
public class ResponseManager {
    public ResponseManager() {}

    /**
     * Shows message on console
     * @param prompt Message
     */
    public void showMessage(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
    }

    /**
     * Shows message on console if it's currently in interactive mode
     * @param prompt
     * @param interactiveMode
     */
    public void showMessage(String prompt, boolean interactiveMode) {
        if (interactiveMode) {
            showMessage(prompt);
        }
    }

    /**
     * Shows response (message + EOL symbol) on console
     * @param message Response
     */
    public void showResponse(String message) {
        if (message != null && !message.isEmpty())
            showMessage(message +"\n");
    }

    /**
     * Shows response on console if it's currently running in interactive mode
     * @param message
     * @param interactiveMode
     */
    public void showResponse(String message, boolean interactiveMode) {
        if (interactiveMode)
            showResponse(message);
    }

    /**
     * Returns an error message if the program is running in interactive mode,
     * throws a ScriptErrorException if the throwException parameter is set to true and the program is
     * running in script mode
     * @param message
     * @param interactiveMode
     * @param throwException
     * @return
     * @throws ScriptErrorException
     */
    public String sendErrorMessage(String message, boolean interactiveMode, boolean throwException) throws ScriptErrorException {
        if (interactiveMode) {
            return message;
        }
        if (throwException) {
            throw new ScriptErrorException();
        }
        return null;
    }

    /**
     * Shows the error message
     * @param message
     * @param interactiveMode
     * @param throwException
     * @throws ScriptErrorException
     */
    public void showErrorMessage(String message, boolean interactiveMode, boolean throwException) throws ScriptErrorException {
        showResponse(sendErrorMessage(message, interactiveMode, throwException));
    }
}
