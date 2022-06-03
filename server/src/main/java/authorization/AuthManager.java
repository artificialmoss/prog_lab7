package authorization;

import collection.DBManager;
import command.Response;
import dtos.UserDTO;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AuthManager {
    private final String pepper = "a!fF#8Er";
    private final MessageDigest md = MessageDigest.getInstance("SHA-384");
    private final DBManager dbManager;

    public AuthManager(DBManager dbManager) throws NoSuchAlgorithmException {
        this.dbManager = dbManager;
    }

    public byte[] encryptPassword(String password, String salt) {
        return md.digest((pepper + password + salt).getBytes(StandardCharsets.UTF_8));
    }

    private String generateSalt() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    public boolean matchUsername(UserDTO userDTO) {
        User user = dbManager.getUser(userDTO.getName());
        if (user == null) {
            return false;
        }
        return true;
    }

    public User matchUser(UserDTO userDTO) {
        User user = dbManager.getUser(userDTO.getName());
        if (user == null) {
            return null;
        }
        byte[] dtoPassword = encryptPassword(userDTO.getPassword(), user.getSalt());
        byte[] expectedPassword = user.getPassword();
        if (user.getName().equals(userDTO.getName()) && Arrays.equals(dtoPassword, expectedPassword)) {
            return user;
        } else return null;
    }

    public Response registerUser(UserDTO userDTO, SocketAddress clientAddress) {
        Response res = new Response();
        res.setClientAddress(clientAddress);
        if (!matchUsername(userDTO)) {
            String salt = generateSalt();
            byte[] passwordHash = encryptPassword(userDTO.getPassword(), salt);
            User user = new User(userDTO.getName(), passwordHash, salt);
            int check = dbManager.addUser(user);
            if (check != -1) {
                return res.setSuccessfulResponse("User " + user.getName() + " has been registered.");
            }
        } else {
            return res.setError("Error — user with this username already exists.");
        }
        return res.setError("Unknown error — couldn't register this user");
    }
}
