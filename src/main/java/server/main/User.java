package server.main;

import java.sql.*;
import java.util.UUID;

public class User
{
    private String response;
    public String registration(PreparedStatement pst, String[] info)
    {
        try {
            UUID id = UUID.randomUUID();
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
}
