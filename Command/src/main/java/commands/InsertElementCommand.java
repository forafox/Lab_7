package commands;


/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 22.02.2023 0:31
 */

import collection.CollectionManager;
import collection.LabWork;
import collection.LabWorkFieldValidation;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import io.UserIO;
import workWithFile.LabWorkFieldsReader;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

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
    public void execute(String[] arguments, InvocationStatus invocationStatus, PrintStream printStream, UserData userData, Lock locker) throws CannotExecuteCommandException {
        if (invocationStatus.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length > 1) {
                throw new CannotExecuteCommandException("Количество аргументов у данной команды должно быть не более 1.");
            }
            if (arguments.length == 1) {
                if (LabWorkFieldValidation.validate("id", arguments[0])) {
                    printStream.println("Введите значения полей для элемента коллекции:\n");
                    LabWork labWork = labWorkFieldsReader.read(Integer.parseInt(arguments[0]));
                    labWork.setOwner(userData.getLogin());
                    super.result.add(Integer.parseInt(arguments[0])); //Integer id - result(0), dragon - result(1)
                    super.result.add(labWork);
                } else
                    throw new CannotExecuteCommandException("Введены невалидные аргументы: id = " + arguments[0]);
            }else{
                    printStream.println("Введите значения полей для элемента коллекции:\n");
                    LabWork labWork = labWorkFieldsReader.read();
                    labWork.setOwner(userData.getLogin());
                    super.result.add(labWork);
            }
        } else if (invocationStatus.equals(InvocationStatus.SERVER)) {
            try{
            locker.lock();
            if(result.size()==2) {
                LabWork labWork = (LabWork) this.getResult().get(1);
                if (!cdh.isAnyRowById(labWork.getId())) {
                    cdh.insertRow(labWork);
                    collectionManager.insertWithId((Integer) this.getResult().get(0), labWork, printStream);
                    printStream.println("Элемент добавлен в коллекцию.");
                }
            }else {
                LabWork labWork = (LabWork) this.getResult().get(0);
                if (!cdh.isAnyRowById(labWork.getId())) {
                    labWork.setId(CollectionManager.getRandomId());
                    cdh.insertRow(labWork);
                    collectionManager.insert(labWork);
                    printStream.println("Элемент добавлен в коллекцию.");
                }
            }
        } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                locker.unlock();
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
