package collection;

import collectionData.*;
import log.Log;
import org.apache.logging.log4j.Logger;
import authorization.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Vector;

//todo dao pattern
public class DBManager {
    //private final String url = "jdbc:postgresql://pg:5432/studs"; // для гелиоса
    private final String url = "jdbc:postgresql://localhost:5432/studs"; // для идеи
    private final String username;
    private final String password;
    private final Logger logger = Log.getLogger();
    private Connection connection;

    private final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "userId SERIAL UNIQUE," +
                    "username TEXT PRIMARY KEY," +
                    "password BYTEA NOT NULL," +
                    "salt TEXT);";
    private final String CREATE_COLLECTION_TABLE =
            "CREATE TABLE IF NOT EXISTS collection (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "personName TEXT NOT NULL," +
                    "creationDate TIMESTAMP NOT NULL," +
                    "height BIGINT NOT NULL," +
                    "birthday TIMESTAMP," +
                    "hairColor TEXT NOT NULL," +
                    "nationality TEXT," +
                    "creatorId INTEGER REFERENCES users (userId));";
    private final String CREATE_COORDINATES_TABLE =
            "CREATE TABLE IF NOT EXISTS coordinates (" +
                    "personId BIGINT PRIMARY KEY REFERENCES collection (id) ON delete cascade," +
                    "coordX DOUBLE PRECISION," +
                    "coordY DECIMAL);";
    private final String CREATE_LOCATION_TABLE =
            "CREATE TABLE IF NOT EXISTS location (" +
                    "personId BIGINT PRIMARY KEY REFERENCES collection (id) ON delete cascade," +
                    "locX INTEGER," +
                    "locY DOUBLE PRECISION," +
                    "locName TEXT);";

    private final String GET_COLLECTION =
            "SELECT * FROM collection " +
                "JOIN coordinates ON coordinates.personId = collection.id " +
                "JOIN location ON location.personId = collection.id " +
                "JOIN users ON users.userId = collection.creatorId";
    private final String ADD_PERSON =
            "INSERT INTO collection (personName, creationDate, height, birthday," +
                "hairColor, nationality, creatorId) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private final String ADD_PERSON_LOCATION =
            "INSERT INTO location (personId, locX, locY, locName) VALUES (?, ?, ?, ?);";
    private final String ADD_PERSON_COORDINATES =
            "INSERT INTO coordinates (personId, coordX, coordY) VALUES (?, ?, ?);";
    private final String CLEAR_COLLECTION =
            "DELETE FROM collection WHERE creatorId = ?";
    private final String REMOVE_BY_ID =
            "DELETE FROM collection WHERE creatorId = ? AND id = ?";
    private final String UPDATE_BY_ID_PERSON =
            "UPDATE collection " +
                    "SET personName = ?, height = ?, birthday = ?, hairColor = ?, nationality = ?" +
                    "WHERE creatorId = ? AND id = ?";
    private final String UPDATE_BY_ID_COORDINATES =
            "UPDATE coordinates " +
                    "SET coordX = ?, coordY = ?" +
                    "WHERE personId = ?";
    private final String UPDATE_BY_ID_LOCATION =
            "UPDATE location " +
                    "SET locX = ?, locY = ?, locName = ?" +
                    "WHERE personId = ?";

    //todo one request for creating/updating an element in all tables

    private final String ADD_PERSON_1 =
            "";
    private final String COUNT_BY_BIRTHDAY =
            "SELECT birthday, count(*) FROM collection WHERE birthday = ?;";
    private final String GROUP_BY_BIRTHDAY =
            "SELECT COUNT(*) FROM COLLECTION GROUP BY birthday;";
    private final String GET_BIRTHDAYS =
            "SELECT UNIQUE birthday FROM collection;";

    private final String GET_USER =
            "SELECT * FROM users WHERE username = ?;";
    private final String ADD_USER =
            "INSERT INTO users (username, password, salt) VALUES (?, ?, ?);";

    public DBManager(String username, String password) {
        this.username = username;
        this.password = password;
        startConnection();
    }

    private Connection startConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection(url, username, password);
            return c;
        } catch (ClassNotFoundException e) {
            logger.fatal("No SQL driver found. The application will be closed.");
            System.exit(-1);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.fatal("Can't connect to the database: username and password are invalid. " +
                    "The application will be closed.");
            System.exit(-1);
        }
        return null;
    }

    private Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.fatal("Can't connect to the database: username and password are invalid. " +
                    "The application will be closed.");
            System.exit(-1);
        }
        return null;
    }

    public void startDatabase() {
        try {
            connection = connect();
            Statement st = connection.createStatement();
            st.execute(CREATE_USERS_TABLE);
            st.execute(CREATE_COLLECTION_TABLE);
            st.execute(CREATE_COORDINATES_TABLE);
            st.execute(CREATE_LOCATION_TABLE);
            st.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            logger.fatal("Couldn't access the initial database.");
            System.exit(-1);
        }
    }

    private Person rowToPerson(ResultSet row) {
        try {
            Long id = row.getLong("id");
            String name = row.getString("personName");
            ZonedDateTime creationDate = row.getTimestamp("creationDate").
                    toLocalDateTime().atZone(ZoneId.systemDefault());
            Long height = row.getLong("height");
            LocalDateTime birthday = Person.getBirthdayFromTimestamp(row.getTimestamp("birthday"));
            Color hairColor = Color.valueOf(row.getString("hairColor"));
            Country nationality = Country.getNationalityFromString(row.getString("nationality"));
            int creatorId = row.getInt("creatorId");
            String creatorName = row.getString("username");

            double coordX = row.getDouble("coordX");
            float coordY = row.getFloat("coordY");
            Coordinates coordinates = new Coordinates(coordX, coordY);

            int locX = row.getInt("locX");
            double locY = row.getDouble("locY");
            String locName = row.getString("locName");
            Location location = new Location(locX, locY, locName);

            return new Person(id, name, coordinates, height, birthday, hairColor,
                    nationality, location, creationDate, creatorId, creatorName);
        } catch (SQLException e) {
            return null;
        }
    }

    public Vector<Person> resultSetToCollection(ResultSet resultSet) {
        Vector<Person> collection = new Vector<>();
        try {
            int errorCount = 0;
            while (resultSet.next()) {
                Person p = rowToPerson(resultSet);
                if (p == null) {
                    errorCount++;
                } else {
                    collection.add(p);
                }
            }
            if (errorCount != 0) {
                logger.error("Couldn't read" + errorCount + " entries in the collection.");
            }
            return collection;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return collection;
        }
    }

    public Vector<Person> getCollection() {
        try {
            connection = connect();
            PreparedStatement getCollection = connection.prepareStatement(GET_COLLECTION);
            ResultSet res = getCollection.executeQuery();
            Vector<Person> collection = resultSetToCollection(res);
            getCollection.close();
            connection.close();
            return collection;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public int addPerson(Person p, User user) {
        try {
            connection = connect();
            connection.setAutoCommit(false);
            PreparedStatement addPerson = connection.prepareStatement(ADD_PERSON, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement addCoordinates = connection.prepareStatement(ADD_PERSON_COORDINATES);
            PreparedStatement addLocation = connection.prepareStatement(ADD_PERSON_LOCATION);

            addPerson.setString(1, p.getName());
            addPerson.setTimestamp(2, Timestamp.valueOf(p.getCreationDate().toLocalDateTime()));
            addPerson.setLong(3, p.getHeight());
            addPerson.setTimestamp(4, p.getBirthdayTimestamp());
            addPerson.setString(5, p.getHairColor().name());
            addPerson.setString(6, Country.toDBString(p.getNationality()));
            addPerson.setInt(7, user.getId());

            addCoordinates.setDouble(2, p.getCoordinates().getX());
            addCoordinates.setFloat(3, p.getCoordinates().getY());

            addLocation.setInt(2, p.getLocation().getX());
            addLocation.setDouble(3, p.getLocation().getY());
            addLocation.setString(4, p.getLocation().getName());

            int res1 = addPerson.executeUpdate();
            System.out.println(res1);
            ResultSet resultSet = addPerson.getGeneratedKeys();
            if (resultSet.next()) {
                long personId = resultSet.getLong("id");
                addCoordinates.setLong(1, personId);
                addLocation.setLong(1, personId);
                int res = (addLocation.executeUpdate() + addCoordinates.executeUpdate());
                addLocation.close();
                addCoordinates.close();
                connection.commit();
                connection.close();
                return res;
            } else {
                connection.rollback();
            }
            addPerson.close();
            connection.close();
            return -1;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }

    public int removePerson(Long id, User user) {
        try {
            connection = connect();
            PreparedStatement removePerson = connection.prepareStatement(REMOVE_BY_ID);
            removePerson.setInt(1, user.getId());
            removePerson.setLong(2, id);
            int res = removePerson.executeUpdate();
            removePerson.close();
            connection.close();
            return res;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }

    public int updatePerson(Long id, Person p, User user) {
        try {
            connection = connect();
            connection.setAutoCommit(false);
            PreparedStatement updatePerson = connection.prepareStatement(UPDATE_BY_ID_PERSON);
            PreparedStatement updateCoordinates = connection.prepareStatement(UPDATE_BY_ID_COORDINATES);
            PreparedStatement updateLocation = connection.prepareStatement(UPDATE_BY_ID_LOCATION);

            updatePerson.setString(1, p.getName());
            updatePerson.setLong(2, p.getHeight());
            updatePerson.setTimestamp(3, p.getBirthdayTimestamp());
            updatePerson.setString(4, p.getHairColor().name());
            updatePerson.setString(5, Country.toDBString(p.getNationality()));
            updatePerson.setInt(6, user.getId());
            updatePerson.setLong(7, id);

            int res = updatePerson.executeUpdate();
            if (res == 1) {
                updateCoordinates.setDouble(1, p.getCoordinates().getX());
                updateCoordinates.setFloat(2, p.getCoordinates().getY());
                updateCoordinates.setLong(3, id);
                updateLocation.setInt(1, p.getLocation().getX());
                updateLocation.setDouble(2, p.getLocation().getY());
                updateLocation.setString(3, p.getLocation().getName());
                updateLocation.setLong(4, id);
                res += updateCoordinates.executeUpdate();
                res += updateLocation.executeUpdate();
                updateCoordinates.close();
                updateLocation.close();
            } else {
                connection.rollback();
            }
            updatePerson.close();
            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
            return res;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }

    public int clearCollection(User user) {
        try {
            connection = connect();
            PreparedStatement clear = connection.prepareStatement(CLEAR_COLLECTION);
            clear.setInt(1, user.getId());
            int res = clear.executeUpdate();
            clear.close();
            connection.close();
            return res;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }

    public User getUser(String username) {
        try {
            connection = connect();
            User user = null;
            PreparedStatement getUser = connection.prepareStatement(GET_USER);
            getUser.setString(1, username);
            ResultSet userInfo = getUser.executeQuery();
            if (userInfo.next()) {
                 user = new User(userInfo.getString("username"),
                        userInfo.getBytes("password"),
                        userInfo.getString("salt")).setId(userInfo.getInt("userId"));
            }
            getUser.close();
            connection.close();
            return user;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public int addUser(User user) {
        try {
            connection = connect();
            PreparedStatement addUser = connection.prepareStatement(ADD_USER);
            addUser.setString(1, user.getName());
            addUser.setBytes(2, user.getPassword());
            addUser.setString(3, user.getSalt());
            int res = addUser.executeUpdate();
            System.out.println(res);
            addUser.close();
            connection.close();
            return res;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }
}
