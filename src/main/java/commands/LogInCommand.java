package commands;

import utilites.CollectionManager;
import utilites.User;

public class LogInCommand extends AbstractCommand {
    String message;

    public LogInCommand(User user) {
        super("LogIn", user);
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.logIn(this.getUser());
    }

    @Override
    public String getMessage() {
        return message;
    }
}
