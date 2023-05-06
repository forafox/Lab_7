package dao;

import database.UserData;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 26.04.2023 23:56
 */
@Entity(name="users_test")
@Table(name="users_test", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class UserDAO implements Serializable {
    public UserDAO() {
        this.salt="what_is_it";
    }

    public UserDAO(UserData user) {
            this.passwordDigest=user.getLogin();
            this.passwordDigest=user.getPassword();
    }


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;

    @Column(name="name", length=40, unique=true, nullable=false)
    private String name;

    @Column(name="password_digest", length=64, nullable=false)
    private String passwordDigest;

    @Column(name="salt", length=10)
    private String salt;

    @OneToMany(mappedBy = "creator" ,fetch = FetchType.EAGER)
    private List<LabWorkDAO> labWorks = new ArrayList<>();

    @OneToMany(mappedBy = "creator" ,fetch = FetchType.EAGER)
    private List<LocationDAO> locations = new ArrayList<>();

    @OneToMany(mappedBy = "creator" ,fetch = FetchType.EAGER)
    private List<PersonDAO> persons = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(String passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }



    public void setLabWorks(List<LabWorkDAO> labWorks) {
        this.labWorks = labWorks;
    }

    public List<LocationDAO> getLocationsDAO() {
        return locations;
    }
    public void setLocationsDAO(List<LocationDAO> list){
        this.locations=list;
    }

    public void setPersonsDAO(List<PersonDAO> list) {
        this.persons = list;
    }

    public List<LabWorkDAO> getLabWorksDAO() {
        return labWorks;
    }

    public List<PersonDAO> getPersonsDAO() {
        return persons;
    }
}