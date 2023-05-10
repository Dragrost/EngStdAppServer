package server.main;

import server.comm.GeneralComm;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;

public class Server {

    private static String response;
    private static Connection conn;
    private static void connectToBD() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "Lbvf04Lfd4568520";
        conn = DriverManager.getConnection(url, user, password);
    }
    private static void methodSelector(String request) throws SQLException {
        String[] strings = request.split(",");
        User user = new User();
        String command = strings[0];
        switch (command)
        {
            case "GetID":
                response = user.getID(conn,strings);
                break;
            case "GetLogin":
                response = user.getLogin(conn,strings);
                break;
            case "Registration":
                response = user.registration(conn,strings);
                break;
            case "Login":
                response = user.login(conn,strings);
                break;
            case "getProgress":
                response = String.valueOf(user.getQuantityCorrWords(conn,strings));
                break;
            case "RandomGeneration":
                response = user.randomGeneration(conn);
                break;
            case "AllQuestions":
                response = user.getAllQuestions(conn);
                break;
            case "AddWordsProgress":
                response = user.AddWords(conn, strings);
                break;
            case "Delete":
                response = user.deleteAcc(conn,strings);
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
                        System.out.println(request);
                        methodSelector(request);
                        communication.writeLine(response);
                    } catch (IOException | SQLException e)
                    {
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