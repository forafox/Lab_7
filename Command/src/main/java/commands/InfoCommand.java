package commands;



/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 21.02.2023 23:53
 */

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда, выводящая информацию о коллекции
 */
public class InfoCommand extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;
    /**
     * Конструктор класса.
     *
     * @param collectionManager Хранит ссылку на созданный в объекте Application объект CollectionManager.
     */
    public InfoCommand(CollectionManager collectionManager){

        this.collectionManager=collectionManager;
    }

    /**
     * Конструктор без параметров
     */
    public InfoCommand(){
        super("info");
    }
    /**
     * Метод, исполняющий команду. Выводит описание коллекции TreeMap экземпляров Dragon.
     *
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            if (arguments.length > 0) {
                throw new CannotExecuteCommandException("У данной команды нет аргументов.");
            }

        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            locker.readLock().lock();
            printStream.println(collectionManager.info());
            locker.readLock().unlock();
        }
    }
    /**
     * @return Возвращает описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "команда получает информацию о коллекции(тип, дата инициализации, кол-во элементов, тип элементов коллекции)";
    }
}
