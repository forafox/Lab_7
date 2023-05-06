package dao;

import collection.Difficulty;
import collection.LabWork;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 26.04.2023 23:40
 */
@Entity(name="labWork_test")
@Table(name="labWork_test", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class LabWorkDAO implements Serializable {
    public LabWorkDAO(){

    }
    public LabWorkDAO(LabWork labWork){
        this.name=labWork.getName();
        this.coordinateX=labWork.getCoordinates().getX();
        this.coordinateY=labWork.getCoordinates().getY();
        this.creationDate=labWork.getCreationDate();
        this.minimalPoint=labWork.getMinimalPoint();
        this.maximumPoint=labWork.getMaximumPoint();
        this.personalQualitiesMaximum=labWork.getPersonalQualitiesMaximum();
        this.difficulty=labWork.getDifficulty();
        this.creator=new UserDAO(labWork.getUserData());
    }
    public void update(LabWork labWork){
        this.id=labWork.getId();
        this.name=labWork.getName();
        this.coordinateX=labWork.getCoordinates().getX();
        this.coordinateY=labWork.getCoordinates().getY();
        this.creationDate=labWork.getCreationDate();
        this.minimalPoint=labWork.getMinimalPoint();
        this.maximumPoint=labWork.getMaximumPoint();
        this.personalQualitiesMaximum=labWork.getPersonalQualitiesMaximum();
        this.difficulty=labWork.getDifficulty();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable = false,unique = true,length = 11)
    private int id;
    @NotBlank(message = "Name don't be empty")
    @Column(name="name",nullable = false)
    private String name;
    @Column(name="coordinateX")
    private Long coordinateX;
    @Column(name="coordinateY")
    private Double coordinateY;
    @Column(name="creationDate")
    private ZonedDateTime creationDate;
    @Column(name="minimalPoint")
    private Integer minimalPoint;
    @Column(name="maximumPoint")
    private Double maximumPoint;
    @Column(name="personalQualitiesMaximum")
    private Integer personalQualitiesMaximum;
    @Column(name="difficulty")
    private Difficulty difficulty;

    @OneToOne (optional=false, cascade=CascadeType.MERGE,orphanRemoval = true )
    @JoinColumn (name="location_id")
    private LocationDAO locationDAO;

    @OneToOne (optional=false, cascade=CascadeType.MERGE,orphanRemoval = true)
    @JoinColumn (name="person_id")
    private PersonDAO personDAO;
    @ManyToOne(optional = false, cascade=CascadeType.MERGE)
    @JoinColumn(name="creator", nullable=false)
    private UserDAO creator;

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

    public Long getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(Long coordinateX) {
        this.coordinateX = coordinateX;
    }

    public Double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(Double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Integer minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public Double getMaximumPoint() {
        return maximumPoint;
    }

    public void setMaximumPoint(Double maximumPoint) {
        this.maximumPoint = maximumPoint;
    }

    public Integer getPersonalQualitiesMaximum() {
        return personalQualitiesMaximum;
    }

    public void setPersonalQualitiesMaximum(Integer personalQualitiesMaximum) {
        this.personalQualitiesMaximum = personalQualitiesMaximum;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public UserDAO getOwner() {
        return creator;
    }

    public void setOwner(UserDAO owner) {
        this.creator = owner;
    }

    public LocationDAO getLocationDAO() {
        locationDAO.setOwner(this.getOwner());
        return locationDAO;
    }

    public void setLocationDAO(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
    }

    public PersonDAO getPersonDAO() {
        personDAO.setOwner(this.getOwner());
        return personDAO;
    }

    public void setPersonDAO(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public UserDAO getCreator() {
        return creator;
    }

    public void setCreator(UserDAO creator) {
        this.creator = creator;
    }

}
