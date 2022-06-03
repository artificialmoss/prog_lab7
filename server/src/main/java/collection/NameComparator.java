package collection;

import collectionData.Person;

import java.util.Comparator;

/**
 * Comparator that compares the elements by their name
 */
public class NameComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
