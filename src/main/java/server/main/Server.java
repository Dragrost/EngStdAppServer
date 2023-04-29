package server.main;

import server.comm.GeneralComm;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Server {

    private static Connection conn;
    private static PreparedStatement pst;
    private static void connectToBD() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "Lbvf04Lfd4568520";
        conn = DriverManager.getConnection(url, user, password);
        pst = conn.prepareStatement("insert into registrbd (login, password) values (?, ?);");
    }
    private static void registration (String[] info) throws SQLException {
        pst.setString(1,info[1]);
        pst.setString(2,info[2]);
        System.out.println(info[1]);
        System.out.println(info[2]);
        pst.executeUpdate();
    }
    private static void methodSelector(String request) throws SQLException {
        String[] strings = request.split(",");
        String command = strings[0];
        switch (command)
        {
            case "Registration":
                registration(strings);
                break;
            default:
                System.out.println("Что это за херня?!");
                break;
        }
    }
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8000))
        {
            connectToBD();
            System.out.println("Сервак стартанул");
            while (true) {
                GeneralComm communication = new GeneralComm(server);
                new Thread(() -> {
                    try {
                        String request = communication.readLine();
                        methodSelector(request);
                        String response = "Server info " + request.length();
                        communication.writeLine(response);
                        System.out.println("Request " + request);
                        System.out.println(response);
                    } catch (IOException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }
        catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}