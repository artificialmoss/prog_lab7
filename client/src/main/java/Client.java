import utils.RequestManager;

/**
 * Client main class, contains the main method
 * @author Evgenia Ryzhova
 */
public class Client {
    public static void main(String[] args) {
        int port;
        int defaultPort = 1312;
        int timeout;
        int defaultTimeout = 5000;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                port = defaultPort;
                System.out.print("Invalid port format. ");
            }
        } else {
            port = defaultPort;
            System.out.print("No port specified. ");
        }
        System.out.println("Port set to " + port + ".");

        if (args.length > 1) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                timeout = defaultTimeout;
                System.out.print("Invalid socket timeout format. ");
            }
        } else {
            timeout = defaultTimeout;
            System.out.print("No socket timeout specified. ");
        }
        System.out.println("Socket timeout set to " + timeout + ".");

        RequestManager requestManager = new RequestManager(port, timeout);
        requestManager.run();
    }
}
