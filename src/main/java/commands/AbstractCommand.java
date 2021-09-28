package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Абстрактный класс для команд, содержит методы объекта,имя и описание
 */
public abstract class AbstractCommand implements Serializable {
    private String name;
    private User user;

    public AbstractCommand(String name, User user) {
        this.name = name;
        this.user = user;
    }

    /**
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    public abstract void execute(CollectionManager collectionManager);

    public abstract String getMessage();

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
