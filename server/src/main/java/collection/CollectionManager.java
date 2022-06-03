package collection;

import authorization.User;
import collection.exceptions.CannotInitializeCollectionException;
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
    private volatile Vector<Person> collection;
    private final ZonedDateTime initializationDate;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DBManager dbManager;

    public CollectionManager(DBManager dbManager) {
        collection = new Vector<>();
        initializationDate = ZonedDateTime.now();
        this.dbManager = dbManager;
        updateCollection();
    }

    //todo change group by/count by/print birthdays to sql queries

    /**
     * Gets element with the specified id
     * @param id Id
     * @return Element
     */
    public Person getById(Long id) {
        updateCollection();
        return collection.stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }
    /**
     * Adds new element to the collection
     * @param p Element
     * @return Response message
     */
    public String add(Person p, User user) {
        updateCollection();
        Long id = p.getId();
        if (getById(id) == null) {
            int res = dbManager.addPerson(p, user);
            if (res > 0) return "The element has been added to the collection.";
            throw new CollectionException("SQL error — couldn't add this element to the collection.");
        } else {
            throw new CollectionException("Cannot add this element: a person with this id " + id + " already exists.");
        }
    }

    /**
     * Removes the element with the specified id
     * @param id id
     * @return Response message
     */
    public String removeById(Long id, User user) {
        Optional<Person> person = collection.stream()
                .filter(p -> p.getId().equals(id))
                .findAny();
        if (person.isPresent()) {
            int res = dbManager.removePerson(id, user);
            if (res > 0) return "The element with id " + id + " has been removed.";
            throw new CollectionException("User " + user.getName() + " can't remove element with id " + id + ".");
        }
        return "The element with id " + id + " doesn't exist.";
    }

    /**
     * Clears the collection
     * @return Response message
     */
    public String clear(User user) {
        updateCollection();
        if (getSize() == 0) return "Cannot clear the collection: the collection is already empty.";
        int res = dbManager.clearCollection(user);
        if (res == 0) return "No elements in this collection were created by " + user.getName();
        if (res > 0) return "All elements from this collection created by " + user.getName() + " have been removed.";
        throw new CollectionException("SQL error — couldn't clear the collection.");
    }

    /**
     * Returns the contents of the collection
     * @return The contents of the collection in String format
     */
    public String show() {
        updateCollection();
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
    public String updateById(Long id, Person person, User user) {
        updateCollection();
        if (getById(id) != null && getById(id).getCreatorId() == user.getId()) {
            int res = dbManager.updatePerson(id, person, user);
            if (res > 0) return "Element with id " + id + " has been successfully updated.";
            return "Error — couldn't update this element.";
        }
        if (getById(id) != null) throw new CollectionException("The element with id " + id +
                " can't be updated by this user");
        throw new CollectionException("No element with id " + id + " in the collection.");
    }

    /**
     * Gets the size of the collection
     * @return Collection size
     */
    public int getSize() {
        updateCollection();
        return collection.size();
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
        updateCollection();
        return collection.stream()
                .max(Person::compareTo)
                .orElse(null);
    }

    /**
     * Gets the minimal element of the collection
     * @return Min element
     */
    public Person getMin() {
        updateCollection();
        return collection.stream()
                .min(Person::compareTo)
                .orElse(null);
    }

    /**
     * Groups elements by height, counts size of each group
     * @return Response message
     */
    public String groupByHeight() {
        updateCollection();
        if (collection.size() == 0) {
            return "The collection is empty.";
        }
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
        updateCollection();
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
        updateCollection();
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

    private void updateCollection() {
        Vector<Person> updatedCollection = dbManager.getCollection();
        if (updatedCollection != null) {
            collection = updatedCollection;
        } else throw new CannotInitializeCollectionException();
    }
}
