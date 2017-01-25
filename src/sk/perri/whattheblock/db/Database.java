package sk.perri.whattheblock.db;

import java.io.File;
import java.sql.*;

public class Database
{
    private static Connection c = null;
    public static String INS_PREF = "INSERT INTO history(X, Y, Z, ACTION, BLOCK, PLAYER) VALUES(";

    private Database()
    {

    }

    public static boolean init(String path)
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+path+"blocks.db");
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    public static boolean createTable()
    {
        return insertSql("CREATE TABLE IF NOT EXIST history("+
                "X INT NOT NULL, Y INT NOT NULL, Z INT NOT NULL," +
                "ACTION VARCHAR(150) NOT NULL, BLOCK VARCHAR(100) NOT NULL," +
                "PLAYER VARCHAR(200) NOT NULL);");
    }

    public static boolean insertSql(String sql)
    {
        if(c == null)
            return false;

        Statement st = null;
        try
        {
            st = c.createStatement();
            st.executeUpdate(sql);
            st.close();
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static ResultSet selectSql()
    {
        if(c == null)
            return null;

        try
        {
            Statement st = c.createStatement();
            return st.executeQuery("SELECT * FROM history");
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static boolean clearTable()
    {
        if(c == null)
            return false;

        try
        {
            Statement st = c.createStatement();
            st.executeUpdate("DROP TABLE history");

            createTable();

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static void closeConnection()
    {
        try
        {
            c.close();
        }
        catch (Exception e)
        {
            return;
        }
    }
}
