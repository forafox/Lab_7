package dao;

import database.UserData;
import database.UserStatus;
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

    }

    public UserDAO(UserData user) {
            this.passwordDigest=user.getLogin();
            this.passwordDigest=user.getPassword();
            this.userStatus=user.getUserStatus().toString();
    }


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;

    @Column(name="name", length=40, unique=true, nullable=false)
    private String name;

    @Column(name="password_digest", length=100, nullable=false)
    private String passwordDigest;

    @Column(name="salt", length=8)
    private String salt;

    @Column(name="access",length = 11)
    private String userStatus;

    @OneToMany(mappedBy = "creator" ,fetch = FetchType.EAGER,cascade=CascadeType.MERGE,orphanRemoval = true)
    private List<LabWorkDAO> labWorks = new ArrayList<>();

    @OneToMany(mappedBy = "creator" ,fetch = FetchType.EAGER,cascade=CascadeType.MERGE,orphanRemoval = true)
    private List<LocationDAO> locations = new ArrayList<>();

    @OneToMany(mappedBy = "creator" ,fetch = FetchType.EAGER,cascade=CascadeType.MERGE,orphanRemoval = true)
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
    public UserStatus getUserStatus() {
        return UserStatus.valueOf(userStatus);
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus.toString();
    }

    public List<LabWorkDAO> getLabWorks() {
        return labWorks;
    }

    public List<LocationDAO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDAO> locations) {
        this.locations = locations;
    }

    public List<PersonDAO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonDAO> persons) {
        this.persons = persons;
    }
}