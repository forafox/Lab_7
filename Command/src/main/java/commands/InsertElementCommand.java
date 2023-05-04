package commands;


/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 22.02.2023 0:31
 */

import collection.CollectionManager;
import collection.LabWork;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import workWithFile.LabWorkFieldsReader;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда, добавляющая элемент в коллекцию
 */
public class InsertElementCommand extends Command{
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;
    private CollectionDatabaseHandler cdh;

    /**
     * Поле, хранящее ссылку на объект, осуществляющий чтение полей из указанного в userIO потока ввода.
     */
    private LabWorkFieldsReader labWorkFieldsReader;

    /**
     * Конструктор класса, предназначенный для клиента.
     * @param labWorkFieldsReader Хранит ссылку на объект, осуществляющий чтение полей из указанного в UserIo потока ввода.
     */
    public InsertElementCommand(LabWorkFieldsReader labWorkFieldsReader){
        super("insert");
        this.labWorkFieldsReader=labWorkFieldsReader;
    }
    /**
     * Конструктор класса, предназначенный для серверной части команды.
     *
     * @param collectionManager менеджер коллекции.
     */
    public InsertElementCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.cdh = cdh;
        this.collectionManager = collectionManager;
    }

    /**
     * Конструктор класса, предназначенный для серверной части команды
     * @param collectionManager менеджер коллекции
     */
    public InsertElementCommand(CollectionManager collectionManager) {
    this.collectionManager=collectionManager;
    }
    /**
     * Метод, исполняющий команду. При запуске команды запрашивает ввод указанных полей. При успешном выполнении команды на стороне сервера высветится уведомление о добавлении элемента в коллекцию. В случае критической ошибки выполнение команды прерывается.
     *
     * @param invocationStatus режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationStatus, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException {
        if (invocationStatus.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length > 0) {
                throw new CannotExecuteCommandException("У данной команды нет аргументов.");
            }
           else {
                printStream.println("Введите значения полей для элемента коллекции:\n");
                LabWork labWork = labWorkFieldsReader.read();
                labWork.setOwner(userData.getLogin());
                super.result.add(labWork);
            }
        } else if (invocationStatus.equals(InvocationStatus.SERVER)) {
            try{
            locker.writeLock().lock();
                LabWork labWork = (LabWork) this.getResult().get(0);
                    cdh.insertRowNOTid(labWork);
                    labWork.setId(cdh.getIdByLabWork(labWork));
                    collectionManager.insertWithId(labWork.getId(),labWork,printStream);
                    printStream.println("Элемент добавлен в коллекцию.");
        } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                locker.writeLock().unlock();
            }
        }
    }
    /**
     * Метод, возвращающий описание данной команды.
     * @return Описание данной команды.
     *
     * @see HelpCommand
     */
    @Override
    public String getDescription() {
        return "добавляет элемент с указанным ключом в качестве атрибута";
    }
}
