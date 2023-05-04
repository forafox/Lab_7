package dao;

import collection.Location;
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
    public LocationDAO(Location location){
        this.name=location.getName();
        this.locationX=location.getX();
        this.locationY=location.getY();
    }
    @Column(name="locationX")
    private int locationX;
    @Column(name="locationY")
    private double locationY;
    @Column(name="name")
    private String name;

    @OneToOne (optional=false, mappedBy="locationDAO")
    private LabWorkDAO labWorkDAO;
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
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

}
