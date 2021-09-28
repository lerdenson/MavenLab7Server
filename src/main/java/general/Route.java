package general;

import utilites.User;

import java.io.Serializable;
import java.time.LocalDate;

public class Route implements Comparable<Route>, Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле не может быть null
    private Location to; //Поле может быть null
    private Float distance; //Поле не может быть null, Значение поля должно быть больше 1
    private User user;


    public Route(Integer id, String name, Coordinates coordinates, LocalDate creationDate, Location from, Location to, Float distance, User user) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.user = user;
    }

    public Route(String name, Coordinates coordinates, Location from, Location to, Float distance, User user) {
        this.name = name;
        this.coordinates = coordinates;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.user = user;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Перевод маршрута в вид строки
     *
     * @return string
     */
    @Override
    public String toString() {
        return "\nId: " + id.toString() + "\nНазвание: " + name + "\nКоординаты: x=" + coordinates.getX() +
                "; y=" + coordinates.getY() + "\nВремя создания: " + creationDate + "\nОткуда " + from.toString()
                + "\nКуда " + to.toString() + "\n" + "Расстояние: " + distance + "\n" + "Владелец: " + this.getUser().getLogin() + "\n";
    }

    @Override
    public int compareTo(Route route) {
        return this.distance.compareTo(route.getDistance());
    }
}
