package server.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class Administrator
{
    private Connection conn;
    private ArrayList<String> data = new ArrayList<>();
    private String response = "";

    private void processingStr(String[] info)
    {
        final int DATA = 1, REQUEST = 0;
        for (int i = DATA; i < info.length; i++)
            Collections.addAll(data, info[i].split(" "));
        data.removeAll(Collections.singleton(""));
        if (!info[REQUEST].equals("AddTest"))
            data.removeAll(Collections.singleton("Английский"));
    }
    private int getMaxId(String table) throws SQLException {
        PreparedStatement rs = conn.prepareStatement("SELECT id FROM " + table + " ORDER BY id DESC LIMIT 1");
        ResultSet result = rs.executeQuery();
        result.next();
        return result.getInt(1);
    }
    Administrator(Connection conn) {this.conn = conn;}

    public String addWordToSlovar(String[] info) throws SQLException {
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
    public String addTest(String[] info)
    {
        processingStr(info);

        try {
            int idx = getMaxId("mytests");

            PreparedStatement pst = conn.prepareStatement("insert into mytests (id, question, ans1, ans2, ans3, ans4, typequestion) values (?, ?, ?, ?, ?, ?, ?);");
           // pst.setString(1, data.get(ENGLISH_WORD));
           // pst.setString(2, data.get(TRANSCRIPTION));
           // pst.setString(3, data.get(RUSSIAN_WORD));
            pst.executeUpdate();
            response = "allGood";
        } catch (SQLException e) {
            response = "errorKey";
        }
        return response;
    }
    public String deleteWords(String[] info)
    {
        processingStr(info);

        try{
            //if (data.size() == 0)
                //return "errorNumKey";
            for (int DELETE_NUMBER = 0; DELETE_NUMBER < data.size();DELETE_NUMBER++)
            {
                PreparedStatement pst = conn.prepareStatement("DELETE from engruswords where id = ?");
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
    public String deleteTest(String[] info)
    {
        processingStr(info);
        System.out.println(data);
        response = "AllGood";
        return response;
    }
}
