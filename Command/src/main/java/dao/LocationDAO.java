package dao;

import collection.LabWork;
import collection.Location;
import database.UserData;
import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 26.04.2023 23:41
 */
@Entity(name="locations_test")
@Table(name="locations_test",uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class LocationDAO implements Serializable {
    public LocationDAO(){

    }
    public void update(LabWork labWork){
        this.name=labWork.getPerson().getLocation().getName();
        this.locationX=labWork.getPerson().getLocation().getX();
        this.locationY=labWork.getPerson().getLocation().getY();
    }
    public LocationDAO(LabWork labWork){
        this.name=labWork.getPerson().getLocation().getName();
        this.locationX=labWork.getPerson().getLocation().getX();
        this.locationY=labWork.getPerson().getLocation().getY();
        this.creator=new UserDAO(labWork.getUserData());
    }
    public LocationDAO(String name, int locationX, double locationY, UserData userData){
        this.name=name;
        this.locationX=locationX;
        this.locationY=locationY;
        this.creator=new UserDAO(userData);
    }
    @Column(name="locationX")
    private int locationX;
    @Column(name="locationY")
    private double locationY;
    @Column(name="name")
    private String name;

    @OneToOne (optional=false, mappedBy="locationDAO")
    private LabWorkDAO labWorkDAO;
    @ManyToOne(optional = false, cascade = CascadeType.MERGE )
    @JoinColumn(name="creator", nullable=false)
    private UserDAO creator;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LabWorkDAO getLabWorkDAO() {
        return labWorkDAO;
    }

    public void setLabWorkDAO(LabWorkDAO labWorkDAO) {
        this.labWorkDAO = labWorkDAO;
    }

    public UserDAO getCreator() {
        return creator;
    }

    public void setCreator(UserDAO creator) {
        this.creator = creator;
    }
}
