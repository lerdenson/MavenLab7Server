package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

public class RegisterCommand extends AbstractCommand implements Serializable {
    String message;

    public RegisterCommand(User user) {
        super("Register", user);
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.register(this.getUser());
    }

    @Override
    public String getMessage() {
        return message;
    }
}
