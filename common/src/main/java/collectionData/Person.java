package collectionData;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Person – element of the collection that the application manages.
 */
public class Person implements Comparable<Person>, Serializable {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long height; //Поле не может быть null, Значение поля должно быть больше 0
    private LocalDateTime birthday; //Поле может быть null
    private Color hairColor; //Поле не может быть null
    private Country nationality; //Поле может быть null
    private Location location; //Поле не может быть null
    private int creatorId;
    private String creatorName;

    /**
     * Empty constructor, used for reading the collection from JSON file via Jackson's ObjectMapper.
     */
    public Person(){}

    /**
     * Constructor.
     * @param id id of
     * @param name name
     * @param coordinates coordinates
     * @param height height
     * @param birthday date and time of birth
     * @param hairColor hair color
     * @param nationality nationality
     * @param location location
     */
    public Person(Long id, String name, Coordinates coordinates, Long height, LocalDateTime birthday,
        Color hairColor, Country nationality, Location location)
    {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.height = height;
        this.birthday = birthday;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        creationDate = ZonedDateTime.now();
    }

    public Person(Long id, String name, Coordinates coordinates, Long height, LocalDateTime birthday,
                  Color hairColor, Country nationality, Location location, ZonedDateTime creationDate,
                  int creatorId, String creatorName)
    {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.height = height;
        this.birthday = birthday;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        this.creationDate = creationDate;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
    }

    /**
     * Checks whether the element's fields match their requirements
     * @return True if the requirements are satisfied, false otherwise
     */
    public boolean check() {
        return (id != null && id > 0) && (name != null && !name.trim().isEmpty()) && (coordinates!= null && coordinates.check()) &&
                creationDate != null && (height != null && height > 0) && hairColor != null && (location != null && location.check());
    }

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return this.name; }

    public Coordinates getCoordinates() { return this.coordinates; }

    public ZonedDateTime getCreationDate() { return this.creationDate; }

    public void updateCreationDate() {
        creationDate = ZonedDateTime.now();
    }

    public Long getHeight() { return this.height; }

    public LocalDateTime getBirthday() { return birthday; }

    @JsonIgnore
    public LocalDate getBirthdayDate() {
        if (birthday == null) return null;
        return birthday.toLocalDate();
    }

    public Color getHairColor() { return this.hairColor; }

    public Country getNationality() { return this.nationality; }

    public Location getLocation() { return this.location; }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Person p = (Person) o;
        return (this.id.equals(p.getId()) && this.name.equals(p.getName()) && this.coordinates.equals(p.getCoordinates())
                && this.creationDate.equals(p.getCreationDate()) && this.height.equals(p.getHeight()) && this.birthday.equals(p.getBirthday())
                && this.hairColor.equals(p.getHairColor()) && this.nationality.equals(p.getNationality()) &&
                this.location.equals(p.getLocation()));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Overridden method for comparing two elements
     * @param p Another element
     * @return Result of comparing the elements' heights
     */
    @Override
    public int compareTo(Person p) {
        return this.height.compareTo(p.getHeight());
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "Person:\n\tid: " + id.toString() +
                "\n\tname: " + name +
                "\n\tcoordinates: " + coordinates.toString() +
                "\n\tcreation date: " + creationDate.format(formatter) +
                "\n\theight: " + height.toString() +
                "\n\tbirthday: " + ((birthday == null) ? "—" : birthday.format(formatter)) +
                "\n\thair color: " + hairColor.toString() +
                "\n\tnationality: " + ((nationality == null) ? "—": nationality.toString()) +
                "\n\tlocation: " + location.toString() +
                "\n\tcreator: " + creatorName;
    }

    public Timestamp getBirthdayTimestamp() {
        return (birthday == null) ? null : Timestamp.valueOf(birthday);
    }

    public static LocalDateTime getBirthdayFromTimestamp(Timestamp t) {
        return (t == null) ? null : t.toLocalDateTime();
    }
}
