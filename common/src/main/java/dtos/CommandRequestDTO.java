package dtos;

import collectionData.Person;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Command request data transfer object, contains command name and arguments, sent from client to server
 */
public class CommandRequestDTO implements Serializable {
    private String name = null;
    private Long id = null;
    private LocalDate birthday = null;
    private Boolean scriptMode = null;
    private Person person = null;

    public CommandRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public Person getPerson() {
        return person;
    }

    public Boolean getScriptMode() {
        return scriptMode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setScriptMode(Boolean scriptMode) {
        this.scriptMode = scriptMode;
    }
}
