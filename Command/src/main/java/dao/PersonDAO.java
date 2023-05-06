package dao;

import collection.LabWork;
import collection.Person;
import database.UserData;
import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 26.04.2023 23:40
 */
@Entity(name="persons_test")
@Table(name="persons_test", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class PersonDAO implements Serializable {
    public PersonDAO(){

    }
    public PersonDAO(LabWork labWork){
        this.name=labWork.getPerson().getName();
        this.height=labWork.getPerson().getHeight();
        this.passportId=labWork.getPerson().getPassportID();
        this.creator=new UserDAO(labWork.getUserData());
    }

    public void update(LabWork labWork){
        this.name=labWork.getPerson().getName();
        this.height=labWork.getPerson().getHeight();
        this.passportId=labWork.getPerson().getPassportID();
    }
    @Column(name="name")
    private String name;
    @Column(name="height")
    private float height;
    @Column(name="passportID")
    private String passportId;
    @ManyToOne(optional = false, cascade = CascadeType.MERGE )
    @JoinColumn(name="creator", nullable=false)
    private UserDAO creator;

    @OneToOne(optional=false, mappedBy="personDAO")
    private LabWorkDAO labWorkDAO;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public UserDAO getOwner() {
        return creator;
    }

    public void setOwner(UserDAO owner) {
        this.creator = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;


}

    public UserDAO getCreator() {
        return creator;
    }

    public void setCreator(UserDAO creator) {
        this.creator = creator;
    }

    public LabWorkDAO getLabWorkDAO() {
        return labWorkDAO;
    }

    public void setLabWorkDAO(LabWorkDAO labWorkDAO) {
        this.labWorkDAO = labWorkDAO;
    }
    }
