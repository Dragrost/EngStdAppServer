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
        User user = new User(conn);
        Administrator admin = new Administrator(conn);
        String command = strings[0];
        switch (command) {
            case "GetID" -> response = user.getID(strings);//
            case "GetLogin" -> response = user.getLogin(strings);//
            case "Registration" -> response = user.registration(strings);
            case "Login" -> response = user.login(strings);//
            case "getProgress" -> response = String.valueOf(user.getQuantityCorrWords(strings));
            case "RandomGeneration" -> response = user.randomGeneration();
            case "AllQuestions" -> response = user.getAllQuestions();
            case "AddWordsProgress" -> response = user.AddWords(strings);
            case "Delete" -> response = user.deleteAcc(strings);
            case "GetStatus" -> response = user.getStatus(request);//

            case "AddWords" -> response = admin.addWordToSlovar(strings);
            case "AddTest" -> response = admin.addTest(strings);
            case "DeleteWords" -> response = admin.deleteWords(strings);
            case "DeleteTest" -> response = admin.deleteTest(strings);
            case "checkAverageProgress" -> response = admin.checkAverageProgress();
            case "getQuantityWords" -> response = String.valueOf(admin.getMaxId(strings[1]));
            case "getQuantityWordsTest" -> response = String.valueOf(admin.getQuantityWordsTest(strings[1]));
            default -> System.out.println("Что-то не то");
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