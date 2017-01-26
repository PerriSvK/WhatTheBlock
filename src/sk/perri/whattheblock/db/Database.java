package sk.perri.whattheblock.db;

import java.sql.*;

public class Database
{
    private static Connection c = null;
    public static String INS_PREF = "INSERT INTO history(X, Y, Z, ACTION, BLOCK, PLAYER) VALUES(";
    private static String path = "";

    private Database()
    {

    }

    public static String init(String path)
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+path+"blocks.db");
            Database.path = path;
        }
        catch (Exception e)
        {
            return "Database connection error: "+e.toString();
        }

        return "Connection with database established successfully!";
    }

    public static String createTable()
    {
        insertSql("CREATE TABLE IF NOT EXISTS history("+
                "X INT NOT NULL, Y INT NOT NULL, Z INT NOT NULL," +
                "ACTION VARCHAR(150) NOT NULL, BLOCK VARCHAR(100) NOT NULL," +
                "PLAYER VARCHAR(200) NOT NULL);");
        try
        {
            c.close();
        }
        catch (Exception e)
        {
            return "Error creating table: "+e.toString();
        }

        return "Table created successfully";
    }

    public static String insertSql(String sql)
    {
        if(c == null)
            return "Error connect to database!";
        Statement st;
        try
        {
            st = c.createStatement();
            st.executeUpdate(sql);
            st.close();
        }
        catch (Exception e)
        {
            return "Write error: "+e.toString();
        }
        return "Writing completed!";
    }

    public static ResultSet selectSql(int x, int y, int z)
    {
        if(c == null)
            return null;

        try
        {
            Statement st = c.createStatement();
            return st.executeQuery("SELECT * FROM history WHERE X="+x+" AND Y="+y+" AND Z="+z+";");
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
            init(path);

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
        catch (Exception ignored)
        {
        }
    }
}
