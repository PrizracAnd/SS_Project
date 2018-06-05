package db_support;


import java.sql.*;

class MakeDBFile implements IConstants {
    //-----Supports_objects_begin-------------------
    private Connection connection;
    //-----Supports_objects_end-------------------


    public static final String NAME_TABLE_USERS = "users";
    public static final String NAME_TABLE_KEYS = "keys";


    //-----Create_instruction_begin-----------------
    private final String SQL_CREATE_TABLE_USERS =
        "DROP TABLE IF EXISTS " + NAME_TABLE_USERS + ";" +
        "CREATE TABLE " + NAME_TABLE_USERS +
        "(id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +        //id пользователя
        " login  NCHAR(55)  NOT NULL," +                            //login пользователя
        " ip_address CHAR(15)," +                                   //ip адресс пользователя
        " isWorking CHAR(1) NOT NULL);";                            //признак легитивности пользователя (Y/N)

    private final String SQL_CREATE_TABLE_KEYS =
            "DROP TABLE IF EXISTS " + NAME_TABLE_KEYS + ";" +
                    "CREATE TABLE " + NAME_TABLE_KEYS +
                    "(id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +    //id ключа
                    " public  NCHAR(100)  NOT NULL," +                      //publicKey
                    " secret  NCHAR(100)  NOT NULL,";                       //secretKey
    //-----Create_instruction_end-------------------

    //-----Inserted_instruction_begin---------------                //--может быть использовано для ввода тестовых данных
//    final String SQL_INSERT_MIKE =
//        "INSERT INTO " + NAME_TABLE +
//        " (login, passwd) " +
//        "VALUES ('mike', 'qwe');";
//    final String SQL_INSERT_JONH =
//        "INSERT INTO " + NAME_TABLE +
//        " (login, passwd) " +
//        "VALUES ('john', 'rty');";
    //-----Inserted_instruction_end-----------------


    //-----Queries_instruction_begin----------------
    private final String SQL_IS_TABLE_EXISTS = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='";
//    final String SQL_SELECT = "SELECT * FROM " + NAME_TABLE + ";";
    //-----Queries_instruction_end------------------





    public MakeDBFile() {

    }

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


    public void makeDb(){
        try {
        Statement stmt = setConnection();

            if(stmt  != null) {
                // create table
                stmt.executeUpdate(SQL_CREATE_TABLE_USERS);

                // insert record(s)                         //--может быть использовано для ввода тестовых данных
//                stmt.executeUpdate(SQL_INSERT_MIKE);
//                stmt.executeUpdate(SQL_INSERT_JONH);

                stmt.close();
            }
            if(this.connection != null) {
                endConnection(this.connection);
            }
        } catch (SQLException ex) {
                ex.printStackTrace();
        }
    }

    public void makeDbIsNotExists(){
        try {
            int i = 0;
            Statement stmt = setConnection();
            if (stmt != null) {
                ResultSet rs = stmt.executeQuery(SQL_IS_TABLE_EXISTS + NAME_TABLE_USERS + "';");
                i = rs.getInt(0);
                rs.close();
                stmt.close();
            }
            this.connection.close();

            if(i == 0){
                makeDb();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}