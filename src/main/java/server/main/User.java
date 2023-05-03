package server.main;

import java.sql.*;
import java.util.UUID;

public class User
{
    private final int MAX_WORDS = 1025;
    private String response = "";
    public String registration(Connection conn, String[] info)
    {
        try {
            UUID id = UUID.randomUUID();
            PreparedStatement pst = conn.prepareStatement("insert into registrbd (id, login, password) values (?, ?, ?);");
            pst.setString(1, String.valueOf(id));
            pst.setString(2,info[1]);
            pst.setString(3,info[2]);
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
            while (i < MAX_QUESTIONS)
            {
                randKey = (int)(Math.random() * MAX_WORDS) + 1;
                if (!similarId.contains(String.valueOf(randKey)))
                {
                    PreparedStatement rs = conn.prepareStatement("SELECT engwords, ruswords FROM engruswords WHERE id = '" + randKey + "'");
                    ResultSet result = rs.executeQuery();
                    result.next();
                    response += result.getString(1) + " ";
                    response += result.getString(2);
                    response += "||";
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
}
