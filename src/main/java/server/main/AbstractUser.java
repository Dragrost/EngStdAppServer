package server.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AbstractUser {
    Connection conn;
    protected String response = "";
    AbstractUser(Connection connection) {conn = connection;}

    /**
     * Получение ID по логину
     * @param info
     * @return
     */
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

    /**
     * Получение логина по ID
     * @param info
     * @return
     */
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

    /**
     * Проверка на правильность введённого пароля
     * @param info
     * @return
     */
    public String loginInfo(String[] info)
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

    /**
     * Получить статус пользователя
     * @param request
     * @return
     */
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

    /**
     * Получение максимального ID
     * @param table
     * @return
     */
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

    /**
     * Получить количество готовых тестов
     * @param test
     * @return
     */
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

    /**
     * Получить ID слова
     * @param word
     * @return
     */
    private int getWordID(String word)
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT id FROM engruswords WHERE engwords = '" + word + "' OR ruswords = '" + word +"'");
            ResultSet result = rs.executeQuery();
            result.next();
            return result.getInt(1);

        } catch (SQLException e) {
            response = "errorKey";
        }
        return 0;
    }

    /**
     * Сверяет ID 2х слов
     * @param words
     * @return
     */
    public String checkWordsID(String[] words)
    {
        System.out.println(getWordID(words[1]) + "/" + getWordID(words[2]));
        return (getWordID(words[1]) == getWordID(words[2])) ? "allGood" : "errorKey";
    }

    /**
     * Получение перевода и самого слова
     * @param word
     * @return
     */
    public String getWordFromWord(String word)
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT engwords,ruswords FROM engruswords WHERE engwords = '" + word + "' OR ruswords = '" + word +"'");
            ResultSet result = rs.executeQuery();
            result.next();
            response = result.getString(1) + "!" + result.getString(2);

        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }
}
