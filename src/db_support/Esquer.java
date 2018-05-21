package db_support;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Esquer implements IConstants {

    private Connection connection;

    private Statement setConnection(){
        try {
            // loads a class, including running its static initializers
            Class.forName(IConstants.DRIVER_NAME);
            // attempts to establish a connection to the given database URL
            this.connection = DriverManager.getConnection(IConstants.SQLITE_DB);
            // сreates an object for sending SQL statements to the database
            return connection.createStatement();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private void endConnection(Connection connection){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public long insertRecord(String login, @Nullable String ip_address, boolean isWork){
    public void insertRecord(Account account){  // <-- сразу передаем в метод объект db_support.Account
        long id = -1;

        if(account.getUserName() == null){     // <-- доп проверка, если имя пользователя пустое, то сразу возвращаем -1 и выходим из метода
            account.setIdDb(id);
            return;
        }

        //-----Preparing fields begin-------------------------------
        String ch = "'";

        String isWorkStr;
        if(account.isWorking()){
            isWorkStr ="'Y'";
        }else isWorkStr = "'N'";

        String login = ch + account.getUserName() + ch;

        String ip_address = account.getIpAddress();
        if(ip_address == null){     // <-- некорректно пустоту заменять на '', потому, если пустота, то NULL
            ip_address = "NULL";
        }else {
            ip_address = ch + ip_address + ch;
        }
        //-----Preparing fields end---------------------------------

        Statement stmt = setConnection();

        if(stmt != null){
            try {
                stmt.executeUpdate(
                        "INSERT INTO " + MakeDBFile.NAME_TABLE_USERS +
                                " (login" +
                                ", ip_address" +
                                ", isWorking) " +
                                "VALUES (" + login +
                                ", " + ip_address +
                                ", " + isWorkStr + ");"
                );
                ResultSet rs = stmt.executeQuery("SELECT id FROM " + MakeDBFile.NAME_TABLE_USERS + " WHERE rowid=last_insert_rowid();");
                if (rs.next()) {    // <-- доп проверка, что запись выборки существует
                    id = rs.getLong(0);
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if(this.connection != null) endConnection(this.connection);

//        return id; // <-- теперь не актуально, возращаем id прямо в объект:
        account.setIdDb(id);
    }

//    public void updateRecord(long id_db, String login, @Nullable String ip_address, boolean isWork){
    public void updateRecord(Account account){

        if(account.getUserName() == null){     // <-- доп проверка, если имя пользователя пустое, то сразу выходим из метода
            return;
        }

        //-----Preparing fields begin-------------------------------
        String ch = "'";

        String isWorkStr;
        if(account.isWorking()){
            isWorkStr ="'Y'";
        }else isWorkStr = "'N'";

        String login = ch + account.getUserName() + ch;

        String ip_address = account.getIpAddress();
        if(ip_address == null){     // <-- некорректно пустоту заменять на '', потому, если пустота, то NULL
            ip_address = "NULL";
        }else {
            ip_address = ch + ip_address + ch;
        }
        //-----Preparing fields end---------------------------------

        Statement stmt = setConnection();

        if(stmt != null){
            try {
                stmt.executeUpdate(
                        "UPDATE " + MakeDBFile.NAME_TABLE_USERS + " SET " +
                                " login = " + login +
                                ", ip_address = " + ip_address +
                                ", isWorking = " + isWorkStr +
                                "WHERE id = " + account.getIdDb() + ";"
                );

                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(this.connection != null) endConnection(this.connection);
    }

    public void deleteAllRecord(){
        Statement stmt = setConnection();
        if(stmt != null){
            try {
                stmt.executeUpdate("DELETE FROM " + MakeDBFile.NAME_TABLE_USERS + ";");

                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(this.connection != null) endConnection(this.connection);
    }

    public void deleteRecord(long id){
        Statement stmt = setConnection();
        if(stmt != null){
            try {
                stmt.executeUpdate("DELETE FROM " + MakeDBFile.NAME_TABLE_USERS + "WHERE id = " + id + ";");

                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(this.connection != null) endConnection(this.connection);
    }

    public List<Account> getAllRecordsAccounts(){
        List<Account> la = new ArrayList<Account>();    // <-- нельзя создать объект класса List напрямую,
                                                        // однако, можно создать его путем создания его потомка
                                                        // с дальнешим восходящим преобразованием (принцип восходящего преобразования - любой объект потомок, может быть использован как родитель,
                                                        // т.к. при наследовании идет расширение функционала родителя)

        Statement stmt = setConnection();
        if (stmt != null){
            try {
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + MakeDBFile.NAME_TABLE_USERS + ";");
                while (rs.next()){
                    boolean isWorking = false;                                      // <-- эти две строки - преобразование
                    if(rs.getString(3).equals("Y")) isWorking = true;   // из текста в логику

                    // Создаем новый объект аккаунта и тут же заполняем его данными из БД (используем специальный конструктор объекта):
                    Account account = new Account(rs.getString(1), rs.getLong(0), rs.getString(2), isWorking);
                    // Добавляем объект в List:
                    la.add(account);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        return la;
    }

}
