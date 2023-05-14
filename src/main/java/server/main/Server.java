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

    private static String getStatus(String request)
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
    private static void methodSelector(String request) throws SQLException {
        String[] strings = request.split(",");
        User user = new User();
        Administrator admin = new Administrator(conn);
        String command = strings[0];
        switch (command) {
            case "GetID" -> response = user.getID(conn, strings);//
            case "GetLogin" -> response = user.getLogin(conn, strings);//
            case "Registration" -> response = user.registration(conn, strings);
            case "Login" -> response = user.login(conn, strings);//
            case "getProgress" -> response = String.valueOf(user.getQuantityCorrWords(conn, strings));
            case "RandomGeneration" -> response = user.randomGeneration(conn);
            case "AllQuestions" -> response = user.getAllQuestions(conn);
            case "AddWordsProgress" -> response = user.AddWords(conn, strings);
            case "Delete" -> response = user.deleteAcc(conn, strings);
            case "GetStatus" -> response = getStatus(request);//

            case "AddWords" -> response = admin.addWordToSlovar(strings);
            case "AddTest" -> response = admin.addTest(strings);
            case "DeleteWords" -> response = admin.deleteWords(strings);
            case "DeleteTest" -> response = admin.deleteTest(strings);
            case "checkAverageProgress" -> response = admin.checkAverageProgress();
            case "getQuantityWords" -> response = String.valueOf(admin.getMaxId("engruswords"));
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