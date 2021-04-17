import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabase {

    static String path = "localhost:1433";
    static String url = "jdbc:sqlserver://" + path + ";databaseName=gainSON";
    static String user = "reader";
    static String password = "gains4all";

    /**
     * Connects to the gainSON database and executes a statement
     * @param sql as a String query to be executed.
     * @return databaseResult as a ResultSet to be parsed later.
     */
    public static ResultSet connectAndExecuteSQL(String sql) {

        ResultSet databaseResult = null;

        try {
            //Connect to gainSON database
            Connection myConnection = DriverManager.getConnection(url, user, password);

            //System.out.println("Successfully connected to database. Executing: " + sql);

            //Execute statement
            Statement myStatement = myConnection.createStatement();
            databaseResult = myStatement.executeQuery(sql);

        } catch (SQLException e) {
            System.out.println("ERROR in Database Connection: " + e);
        }
        return databaseResult;
    }
}