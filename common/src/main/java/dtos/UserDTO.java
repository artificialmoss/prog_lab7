package dtos;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private final String name;
    private final String password;

    public UserDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
