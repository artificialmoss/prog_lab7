package collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import collectionData.Person;
import log.Log;
import utils.GetFile;
import collection.exceptions.CannotInitializeCollectionException;
import utils.exceptions.NoFileException;

import java.io.*;
import java.util.Vector;

/**
 * Class for parsing json files into collections and vice versa
 */
public class JsonParser implements GetFile {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String filepath;
    private final String defaultPath;

    public JsonParser(String filepath, String defaultPath) {
        this.filepath = filepath;
        this.defaultPath = defaultPath;
    }

    /**
     * Parses a json string into a collection
     *
     * @param json Json string
     * @return Collection manager that manages the collection stored in json string
     * @throws CannotInitializeCollectionException Thrown when the collection can't be initialized
     */
    public CollectionManager jsonStringToCollection(String json) throws CannotInitializeCollectionException {
        try {
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            Vector<Person> collection = mapper.readValue(json, new TypeReference<Vector<Person>>() {});
            return new CollectionManager().initializeCollection(collection);
        } catch (IOException e) {
            throw new CannotInitializeCollectionException();
        }
    }

    /**
     * Converts a collection into a json string
     *
     * @param collection The collection
     * @return The resulting string
     * @throws JsonProcessingException Thrown when the collection can't be processed into json
     */
    public String collectionToJsonString(Vector<Person> collection) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collection);
    }

    /**
     * Converts the contents of the file into a string
     *
     * @return The resulting string
     */
    public String fileToString() {
        try {
            getReadableFile(filepath);
            StringBuilder fileContent = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
            String line = br.readLine();
            while (line != null) {
                fileContent.append(line);
                line = br.readLine();
            }
            br.close();
            return fileContent.toString();
        } catch (NoFileException | IOException e) {
            throw new CannotInitializeCollectionException();
        }
    }

    /**
     * Writes a string to a file
     *
     * @param s The string
     * @return The absolute path of the file the string has been written into
     * @throws NoFileException Thrown when neither the first nor the second filepath result in a writable file
     */
    public String writeStringToFile(String s) throws NoFileException {
        try {
            File file = getWritableFile(filepath, defaultPath);
            PrintWriter p = new PrintWriter(file);
            p.print(s);
            p.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            throw new NoFileException();
        }
    }

    /**
     * Converts a json file into a collection
     *
     * @return Collection manager for the resulting collection
     */
    public CollectionManager fileToCollection() {
        try {
            return jsonStringToCollection(fileToString());
        } catch (CannotInitializeCollectionException e) {
            Log.getLogger().error("Cannot initialize collection" +
                    ((filepath == null) ? " — enviroment variable LAB5_PATH is not set" :
                    (" stored in " + filepath)) + ". The collection will be empty.");
            return new CollectionManager();
        }
    }

    /**
     * Saves the collection to a file in json format
     *
     * @param collection The collection
     * @return The result
     */
    public String writeCollectionToFile(Vector<Person> collection) {
        try {
            String filepath = writeStringToFile(collectionToJsonString(collection));
            return "The collection has been saved to " + filepath + ".";
        } catch (JsonProcessingException e) {
            return "Unknown error: can't convert this collection to json.";
        } catch (NoFileException e) {
            return "Can't create/open/write in the destination file.";
        }
    }
}
