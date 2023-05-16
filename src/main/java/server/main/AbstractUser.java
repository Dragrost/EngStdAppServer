package server.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AbstractUser {
    Connection conn;
    protected String response = "";
    AbstractUser(Connection connection) {conn = connection;}
    public String getID(String[] info)
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT id FROM registrbd WHERE login = '" + info[1] + "'");
            ResultSet result = rs.executeQuery();
            result.next();

            response = result.getString(1);
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }
    public String getLogin(String[] info)
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT login FROM registrbd WHERE id = '" + info[1] + "'");
            ResultSet result = rs.executeQuery();
            result.next();

            response = result.getString(1);
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }

    public String login(String[] info)
    {
        try{
            PreparedStatement rs = conn.prepareStatement("SELECT password FROM registrbd WHERE login = '" + info[1] + "'");
            ResultSet result = rs.executeQuery();

            result.next();
            response = (result.getString(1).equals(info[2])) ? "allGood" : "wrongPassword";

        }catch (SQLException e)
        {
            response = "errorKey";
        }
        System.out.println(response);
        return response;
    }

    public String getStatus(String request)
    {
        String[] info = request.split(",");
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT status FROM registrbd where id = '" + info[1] + "'");
            ResultSet result = rs.executeQuery();
            result.next();
            return result.getString(1);
        } catch (SQLException e) {
            return "errorKey";
        }
    }
    public int getMaxId(String table)
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT id FROM " + table + " ORDER BY id DESC LIMIT 1");
            ResultSet result = rs.executeQuery();
            result.next();
            return result.getInt(1);
        }
        catch (SQLException e){
            return -1;
        }
    }
    public String getQuantityWordsTest(String test)
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT cardinality(question) FROM mytests where id = '" + test + "'");
            ResultSet result = rs.executeQuery();
            result.next();

            response = String.valueOf(result.getInt(1));
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }
}
