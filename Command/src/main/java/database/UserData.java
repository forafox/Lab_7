package database;

import commands.abstr.CommandContainer;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 03.04.2023 23:54
 */
public class UserData implements Serializable {
    private String login;
    private String password;
    private boolean isNewUser;
    private boolean isConnected;
    private InetAddress inetAddress;
    private Integer port;
    private String salt;

    private UserStatus userStatus;
    private String adminCheckPassword=null;
    private CommandContainer commandContainer;

    public UserData(boolean isNewUser) {
        this.isNewUser = isNewUser;
        this.isConnected = false;
    }
    public UserData(String login,String password){
        this.login=login;
        this.password=password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean value) {
        this.isNewUser = value;
    }

    public CommandContainer getCommandContainer() {
        return commandContainer;
    }

    public void setCommandContainer(CommandContainer commandContainer) {
        this.commandContainer = commandContainer;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean value) {
        this.isConnected = value;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "login='" + login + '\'' +
                '}';
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAdminCheckPassword() {
        return adminCheckPassword;
    }

    public void setAdminCheckPassword(String adminCheckPassword) {
        this.adminCheckPassword = adminCheckPassword;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
