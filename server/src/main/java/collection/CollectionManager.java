package collection;

import collection.exceptions.CollectionException;
import collectionData.Person;
import log.Log;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that manages the collection
 */
public class CollectionManager {
    private Vector<Person> collection;
    private final ZonedDateTime initializationDate;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public CollectionManager() {
        collection = new Vector<>();
        initializationDate = ZonedDateTime.now();
    }

    /**
     * Gets element with the specified id
     * @param id Id
     * @return Element
     */
    public Person getById(Long id) {
        return collection.stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    /**
     * Generates the next available id for new element
     * @return Id
     * @throws CollectionException Thrown when the collection is full
     */
    public Long generateNextId() throws CollectionException {
        Long maxId = getMaxId();
        Long nextId = (maxId.equals(Long.MAX_VALUE)) ? 1L : maxId + 1;
        Long start = nextId;
        while (getById(nextId) != null) {
            nextId = (nextId.equals(Long.MAX_VALUE)) ? 1L : nextId + 1;
            if (nextId.equals(start)) {
                throw new CollectionException("Cannot add new element: the collection is full, " +
                        "new id cannot be generated.");
            }
        }
        return nextId;
    }

    /**
     * Returns maximal id in the collection
     * @return Max id
     */
    private Long getMaxId() {
        return collection.stream()
                .mapToLong(Person::getId)
                .max().orElse(0L);
    }

    /**
     * Adds new element to the collection
     * @param p Element
     * @return Response message
     */
    public String add(Person p) {
        Long id = p.getId();
        if (getById(id) == null) {
            collection.add(p);
            return "The element has been added to the collection.";
        } else {
            throw new CollectionException("Cannot add this element: a person with this id " + id + " already exists.");
        }
    }

    /**
     * Removes the element with the specified id
     * @param id id
     * @return Response message
     */
    public String removeById(Long id) {
        Optional<Person> person = collection.stream()
                .filter(p -> p.getId().equals(id))
                .findAny();
        if (person.isPresent()) {
            collection.remove(person.get());
            return "The element with id " + id + " has been removed.";
        }
        return "The element with id " + id + " doesn't exist.";
    }

    /**
     * Clears the collection
     * @return Response message
     */
    public String clear() {
        if (getSize() == 0) return "Cannot clear the collection: the collection is already empty.";
        collection.clear();
        return "The collection has been cleared.";
    }

    /**
     * Returns the contents of the collection
     * @return The contents of the collection in String format
     */
    public String show() {
        if (getSize() == 0) {
            return "The collection is empty.";
        }
        StringBuilder res = new StringBuilder();
        collection.stream()
                .sorted(new NameComparator())
                .forEachOrdered(p -> res.append(p.toString()).
                        append("\n"));
        return res.toString();
    }

    /**
     * Updates the element with the specified id
     * @param id Id
     * @param person Replacement
     */
    public void updateById(Long id, Person person) {
        collection = collection.stream()
                .map(p -> p.getId().equals(id) ? person : p)
                .collect(Collectors.toCollection(Vector::new));
    }

    /**
     * Gets the size of the collection
     * @return Collection size
     */
    public int getSize() {
        return (int) collection.stream().count();
    }

    /**
     * Returns the type of the collection
     * @return Collection type name
     */
    public String getType() {
        return collection.getClass().getSimpleName();
    }

    /**
     * Gets the initialization date of the collection
     * @return Initialization date
     */
    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return initializationDate.format(formatter);
    }

    /**
     * Gets the element type of the collection
     * @return Element type name
     */
    public String getElementType() {
        return Person.class.getSimpleName();
    }

    /**
     * Gets the maximal element of the collection
     * @return Max element
     */
    public Person getMax() {
        return collection.stream()
                .max(Person::compareTo)
                .orElse(null);
    }

    /**
     * Gets the minimal element of the collection
     * @return Min element
     */
    public Person getMin() {
        return collection.stream()
                .min(Person::compareTo)
                .orElse(null);
    }

    /**
     * Groups elements by height, counts size of each group
     * @return Response message
     */
    public String groupByHeight() {
        StringBuilder res = new StringBuilder();
        collection.stream()
                .collect(Collectors.groupingBy(Person::getHeight, Collectors.counting()))
                .forEach((key, value) -> res.append("Height: ").
                        append(key).
                        append(", number of people with this height: ").
                        append(value).append("\n"));
        return res.toString();
    }

    /**
     * Gets the set of all birthday
     * @return List of all (unique) birthdays in descending order
     */
    public String descendingBirthdays() {
        if (getSize() == 0) {
            return "Cannot print birthdays — the collection is empty.";
        }
        StringBuilder birthdays  = new StringBuilder();
        collection.stream()
                .map(Person::getBirthdayDate)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .forEachOrdered(date -> birthdays.append(date.format(dateFormatter))
                        .append(" "));
        String res = birthdays.toString();
        if (res.trim().isEmpty()) {
            return "None of the elements of this collection have their birthdays specified.";
        }
        return "Elements' birthdays: " + res;
    }

    /**
     * Counts elements with the specified birthday
     * @param birthday Birthday
     * @return Response message
     */
    public String countBirthday(LocalDate birthday) {
        int count = (int) collection.stream()
                .filter(p -> p.getBirthdayDate() != null)
                .filter(p -> p.getBirthdayDate().equals(birthday))
                .count();
        return count + " people in the collection were born on " + birthday.format(dateFormatter) + ".";
    }

    /**
     * Shuffles the collection
     */
    public void shuffle() {
        Collections.shuffle(collection);
    }

    private void checkAndRemove() {
        Set<Long> ids = new HashSet<>();
        collection = collection.stream()
                .filter(p -> !(ids.contains(p.getId()) && p.check()))
                .peek(p -> ids.add(p.getId()))
                .collect(Collectors.toCollection(Vector::new));
    }

    /**
     * Initializes a new collection for this collection manager
     * @param collection Collection
     * @return The collection manager
     */
    public CollectionManager initializeCollection(Vector<Person> collection) {
        if (collection != null) {
            this.collection = collection;
            checkAndRemove();
            Log.getLogger().info("The collection has been initialized.");
        }
        return this;
    }

    /**
     * Calls parser to save the collection to a json file
     * @param parser Parser
     * @return Response message
     */
    public String save(JsonParser parser) {
        return parser.writeCollectionToFile(collection);
    }
}
