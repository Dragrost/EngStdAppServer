package server.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class Administrator extends AbstractUser
{
    private final ArrayList<String> data = new ArrayList<>();
    Administrator(Connection conn) {super(conn);}
    /**
     * Создание массива. Всего 6 элементов на добавление. 1 - слово вопрос, 5 - слова-ответ и 1 - тип вопроса (Англ, русс).
     * Добавление происходит по столбцам, поэтому шаг через 6.
     * @param numColumn
     * @return
     * @throws SQLException
     */
    private Array createArray(int numColumn) throws SQLException {
        final int STEP = 6;
        String[] dataStr = new String[data.size()/STEP];
        for (int i = numColumn, j = 0; i < data.size(); i+=STEP, j++)
            dataStr[j] = data.get(i);
        return conn.createArrayOf("text",dataStr);
    }

    /**
     * Добавление слов в строке, разделённых пробелом в arraylist, переданных на добавление из клиента.
     * @param info
     */
    private void processingStr(String[] info)
    {
        final int DATA = 1, REQUEST = 0;
        for (int i = DATA; i < info.length; i++)
            Collections.addAll(data, info[i].split(" "));
        data.removeAll(Collections.singleton(""));
        if (!info[REQUEST].equals("AddTest"))
            data.removeAll(Collections.singleton("Английский"));
    }
    /**
     * Добавление слов в словарь
     * @param info
     * @return
     */
    public String addWordToDictionary(String[] info){
        int i = 0;
        processingStr(info);
        int lastIdx = getMaxId("engruswords");
        try {
            while (i < data.size())
            {
                PreparedStatement pst = conn.prepareStatement("insert into engruswords (id, engWords, transcription, rusWords) values (?, ?, ?, ?);");
                pst.setInt(1,++lastIdx);
                pst.setString(2, data.get(i++));
                pst.setString(3, data.get(i++));
                pst.setString(4, data.get(i++));
                pst.executeUpdate();
            }
            response = "allGood";
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }

    /**
     * Добавление теста. В массиве хранятся все слова, полученных от администатора
     * @param info
     * @return
     */
    public String addTest(String[] info)
    {
        processingStr(info);
        try {
            int idx = getMaxId("mytests");

            PreparedStatement pst = conn.prepareStatement("insert into mytests (id, question, ans1, ans2, ans3, ans4, typequestion) values (?, ?, ?, ?, ?, ?, ?);");
            pst.setInt(1, ++idx);
            pst.setArray(2, createArray(0));
            pst.setArray(3, createArray(1));
            pst.setArray(4, createArray(2));
            pst.setArray(5, createArray(3));
            pst.setArray(6, createArray(4));
            pst.setArray(7, createArray(5));
            pst.executeUpdate();
            response = "allGood";
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }

    /**
     * Удаление слов из словаря
     * @param info
     * @return
     */
    public String deleteWords(String[] info) {
        processingStr(info);

        try {
            for (int DELETE_NUMBER = 0; DELETE_NUMBER < data.size();DELETE_NUMBER++)
            {
                PreparedStatement pst = conn.prepareStatement("DELETE from engruswords where id = ?");
                pst.setInt(1, Integer.parseInt(data.get(DELETE_NUMBER)));
                pst.executeUpdate();
            }
            response = "allGood";
        }catch (Exception e) {
            response = "errorNumKey";
        }
        return response;
    }

    /**
     * Удаление теста
     * @param info
     * @return
     */
    public String deleteTest(String[] info)
    {
        processingStr(info);

        try{
            for (int DELETE_NUMBER = 0; DELETE_NUMBER < data.size();DELETE_NUMBER++)
            {
                PreparedStatement pst = conn.prepareStatement("DELETE from mytests where id = ?");
                pst.setInt(1, Integer.parseInt(data.get(DELETE_NUMBER)));
                pst.executeUpdate();
            }
            response = "allGood";
        }catch (Exception e)
        {
            response = "errorNumKey";
        }
        return response;
    }

    /**
     * Получение информации об пользователе
     * @return
     */
    public String getPersonProgress()
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT id,login,progress FROM registrbd WHERE status = 'USER'");
            ResultSet result = rs.executeQuery();
            while (result.next())
            {
                response += "!" + result.getString(1) + "!" + result.getString(2) + "!" + result.getString(3);
            }
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }

    /**
     * Получение таблицы слов
     * @return
     */
    public String getEngTable()
    {
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT id,engwords,ruswords FROM engruswords");
            ResultSet result = rs.executeQuery();
            while (result.next())
            {
                response += "!" + result.getString(1) + "!" + result.getString(2) + "!" + result.getString(3);
            }
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }
}
