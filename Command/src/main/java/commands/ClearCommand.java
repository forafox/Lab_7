package commands;


/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 22.02.2023 0:15
 */

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда, очищающая коллекцию.
 */
public class ClearCommand extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;
    private CollectionDatabaseHandler cdh;
    /**
     * Конструктор класса без аргументов
     */
    public ClearCommand(){
        super("clear");
    }
    /**
     * Конструктор класса
     *
     * @param collectionManager Хранит ссылку на созданный в объекте Application объект CollectionManager.
     */
    /**
     * Конструктор класа, предназначенный для сервера.
     *
     * @param collectionManager менеджер коллекции
     */
    public ClearCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.cdh = cdh;
        this.collectionManager = collectionManager;
    }
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException, SQLException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            if (arguments.length > 0) {
                throw new CannotExecuteCommandException("У данной команды нет аргументов.");
            }
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            try {
                locker.readLock().lock();
                Integer[] ids = cdh.getAllOwner(userData);
                locker.writeLock().lock();
                cdh.deleteAllOwned(userData);
                for (int id : ids) collectionManager.removeKey(id);
                printStream.println("Элементы коллекции, принадлежащие пользователю " + userData.getLogin() + " были удалены.");
            } finally {
                locker.writeLock().unlock();
                locker.readLock().unlock();
            }

        }
    }
    /**
     * @return Описание команды.
     * @see HelpCommand
     */
    @Override
    public String getDescription() {
        return "очищает все элементы коллекции";
    }
}
