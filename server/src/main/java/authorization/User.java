package authorization;

public class User {
    private final String name;
    private final byte[] password;
    private final String salt;
    private int id;

    public User(String name, byte[] password, String salt) {
        this.name = name;
        this.password = password;
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }
}
