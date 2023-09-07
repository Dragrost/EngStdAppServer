package server.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class User extends AbstractUser
{
    private final ArrayList<String> correctWords = new ArrayList<>();
    private final ArrayList<String> incorrectWords = new ArrayList<>();
    private ArrayList<String> researchedWords = new ArrayList<>();

    User(Connection connection) {
        super(connection);
    }

    /**
     * Добавление в arraylist слов
     * @param info
     */
    private void processStrings(String[] info)
    {
        final int CORRECT_WORDS = 2;
        final int INCORRECT_WORDS = 3;
        try {
            Collections.addAll(correctWords, info[CORRECT_WORDS].split("!"));
        } catch (Exception e) {
            correctWords.add("");
        }
        try {
            Collections.addAll(incorrectWords, info[INCORRECT_WORDS].split("!"));
        } catch (Exception e) {
            incorrectWords.add("");
        }
    }

    /**
     * Получение массив ID слов
     */
    private void getWordsID()
    {
        try {
            for (int i = 0; i < correctWords.size();i++)
            {
                PreparedStatement rs = conn.prepareStatement("SELECT id FROM engruswords WHERE engwords = '" + correctWords.get(i) + "' OR ruswords = '" + correctWords.get(i) + "'");
                ResultSet result = rs.executeQuery();
                result.next();
                correctWords.set(i, String.valueOf(result.getInt(1)));
            }
            for (int i = 0; i < incorrectWords.size();i++)
            {
                PreparedStatement rs = conn.prepareStatement("SELECT id FROM engruswords WHERE engwords = '" + incorrectWords.get(i) + "' OR ruswords = '" + incorrectWords.get(i) + "'");
                ResultSet result = rs.executeQuery();
                result.next();
                incorrectWords.set(i, String.valueOf(result.getString(1)));
            }
            response = "allGood";
        } catch (SQLException e) {
            response = "errorKey";
        }
    }

    /**
     * Получение массива исследованных пользователем слов
     * @param info
     * @return
     */
    private ArrayList<String> getResearched(String[] info)
    {
        ArrayList<String> researched = new ArrayList<>();
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT correctwords FROM registrbd where id = '" + info[1] + "'");
            ResultSet result = rs.executeQuery();
            result.next();
            Array arr = result.getArray(1);
            Collections.addAll(researched,(String[]) arr.getArray());
        } catch (SQLException e) {
            response = "errorKey";
        }
        return researched;
    }

    /**
     * Получение количество исследованных слов
     * @param info
     * @return
     */
    public int getQuantityCorrWords(String[] info)
    {
        final int ID = 1;
        try {
            PreparedStatement rs = conn.prepareStatement("SELECT progress FROM registrbd WHERE id = '" + info[ID] + "'");
            ResultSet result = rs.executeQuery();
            result.next();

            return result.getInt(1);
        } catch (SQLException e) {
            response = "errorKey";
        }
        return 0;
    }

    /**
     * Обновление изученных слов пользователей
     * @param info
     */
    private void arrayRebuild(String[] info)
    {
        int progress = getQuantityCorrWords(info);
        researchedWords = getResearched(info);
        for (String correctWord : correctWords) {
            if (!researchedWords.contains(correctWord)) {
                researchedWords.add(correctWord);
                progress++;
            }

        }
        for (String incorrectWord : incorrectWords) {
            if (researchedWords.contains(incorrectWord)) {
                researchedWords.remove(incorrectWord);
                progress--;
            }

        }
        sendData(progress,info);
    }

    /**
     * Обновление данных
     * @param progress
     * @param info
     */
    private void sendData(int progress, String[] info)
    {
        try {
            String[] result = researchedWords.toArray(new String[0]);
            Array addArr = conn.createArrayOf("text",result);

            PreparedStatement pst = conn.prepareStatement("UPDATE registrbd SET correctwords = ? WHERE id = ?");
            pst.setArray(1, addArr);
            pst.setString(2,info[1]);
            pst.executeUpdate();

            pst = conn.prepareStatement("UPDATE registrbd SET progress = ? WHERE id = ?");
            pst.setInt(1,progress);
            pst.setString(2,info[1]);
            pst.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Не получилось обновить данные");
        }
    }
    public String registration(String[] info)
    {
        try {
            PreparedStatement pst = conn.prepareStatement("insert into registrbd (id, login, password, status, progress, correctwords) values (?, ?, ?, ?, ?, ?);");

            String[] result = {};
            Array addArr = conn.createArrayOf("text",result);

            pst.setString(1,info[1]);
            pst.setString(2,info[2]);
            pst.setString(3,info[3]);
            pst.setString(4,"USER");
            pst.setInt(5,0);
            pst.setArray(6,addArr);
            pst.executeUpdate();
            response = "allGood";
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }

    /**
     *
     * @param strings
     * @param info
     * @return
     */
    private String buildResultStr(ArrayList<String>strings,String[] info)
    {
        final int NUMBER_OF_TEST = 1;
        int STEP = Integer.parseInt(getQuantityWordsTest(info[NUMBER_OF_TEST]));
        String result = "";
        for (int i = 0; i < STEP;i++)
            for (int j = i; j < strings.size();j+=STEP)
                result += "!" + strings.get(j);
        return result;
    }

    /**
     *
     * @return
     */
    public String randomGeneration()
    {
        final int MAX_QUESTIONS = 20;
        String similarId = "";
        int randKey;
        try{
            int i = 0;
            while (i < MAX_QUESTIONS*4) {
                randKey = (int)(Math.random() * getMaxId("engruswords")) + 1;
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
        }catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }

    /**
     *
     * @return
     */
    public String getAllQuestions()
    {
        try{
            PreparedStatement rs = conn.prepareStatement("SELECT engwords,ruswords FROM engruswords");
            ResultSet result = rs.executeQuery();
            while (result.next()) {
                response += result.getString(1) + "!";
                response += result.getString(2);
                response += "!";
            }
        }
        catch (SQLException e)
        {
            response = "errorKey";
        }
        return response;
    }

    public String AddWords(String[] info)
    {
        processStrings(info);//Слова превращаем в ID
        getWordsID();//Получаем массив ID слов.
        arrayRebuild(info); //Пробегаемся по каждому слову, если его нет, добавляем в исходный массив.
        return response;
    }

    /**
     * Удаление аккаунта пользователя
     * @param info
     * @return
     */
    public String deleteAccount(String[]info)
    {
        try{
            PreparedStatement pst = conn.prepareStatement("DELETE from registrbd where id = ?");
            pst.setString(1,info[1]);
            pst.executeUpdate();
            response = "allGood";
        }catch (SQLException e)
        {
            response = "errorKey";
        }
        return response;
    }

    /**
     * Получить тест, созданный администратором
     * @param info
     * @return
     */
    public String getAdminTest(String[] info)
    {
        ArrayList<String> list = new ArrayList<>();
        Array arr;
        final int BEGIN = 1, END = 6;
        try{
            PreparedStatement rs = conn.prepareStatement("SELECT question,ans1,ans2,ans3,ans4,typequestion FROM mytests WHERE id = '" +info[1]+ "'");
            ResultSet result = rs.executeQuery();
            result.next();
            for (int i = BEGIN; i <= END;i++) {
                arr = result.getArray(i);
                Collections.addAll(list,(String[]) arr.getArray());
            }
            response += buildResultStr(list, info);
        }catch (SQLException e)
        {
            response = "errorKey";
        }
        return response;
    }

    /**
     * Получить слово с его переводом и транскрипцией
     * @return
     */
    public String getWordsTable()
    {
        try{
            PreparedStatement rs = conn.prepareStatement("SELECT engwords,transcription,ruswords FROM engruswords");
            ResultSet result = rs.executeQuery();
            while (result.next())
            {
                response += result.getString(1) + "!";
                response += result.getString(2) + "!";
                response += result.getString(3) + "!";
            }

        }catch (SQLException e)
        {
            response = "errorKey";
        }
        return response;
    }
}
