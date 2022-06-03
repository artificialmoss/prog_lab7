package collectionData;

import java.io.Serializable;

/**
 * Enum for country options.
 */
public enum Country implements Serializable {
    RUSSIA,
    SPAIN,
    VATICAN,
    ITALY;

    public static Country getNationalityFromString(String s) {
        return (s == null) ? null : Country.valueOf(s);
    }

    public static String toDBString(Country c) {
        return (c == null) ? null : c.toString();
    }
}
