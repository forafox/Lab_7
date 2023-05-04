package commands;

import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 04.05.2023 22:53
 */
public class LogOutCommand extends Command {
    public LogOutCommand (){
        super("log_out");
    }
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException, SQLException {
        //To DO
    }
}
