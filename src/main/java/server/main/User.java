package server.main;
import java.sql.*;

public class User
{
    private final int MAX_WORDS = 1025;
    private String response = "";

    public String getID(Connection conn, String[] info)
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
    public String getLogin(Connection conn, String[] info)
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
    public String registration(Connection conn, String[] info)
    {
        try {
            PreparedStatement pst = conn.prepareStatement("insert into registrbd (id, login, password) values (?, ?, ?);");
            pst.setString(1,info[1]);
            pst.setString(2,info[2]);
            pst.setString(3,info[3]);
            pst.executeUpdate();
            response = "allGood";
        } catch (SQLException e) {
            response = "errorKey";
        }
        System.out.println(info[1]);
        System.out.println(info[2]);
        return response;
    }
    public String login(Connection conn, String[] info)
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
    public String randomGeneration(Connection conn)
    {
        final int MAX_QUESTIONS = 20;
        String similarId = "";
        int randKey;
        try{
            int i = 0;
            while (i < MAX_QUESTIONS*4)
            {
                randKey = (int)(Math.random() * MAX_WORDS) + 1;
                if (!similarId.contains(String.valueOf(randKey)))
                {
                    PreparedStatement rs = conn.prepareStatement("SELECT engwords, ruswords FROM engruswords WHERE id = '" + randKey + "'");
                    ResultSet result = rs.executeQuery();
                    result.next();
                    response += result.getString(1) + "!";
                    response += result.getString(2);
                    response += "!";
                    similarId += randKey + " ";
                    i++;
                }
            }
        }catch (SQLException e)
        {
            response = "errorKey";
        }
        return response;
    }

    public String AddWords(Connection conn, String[] info)
    {
        System.out.println(info);
        return response;
    }
}
