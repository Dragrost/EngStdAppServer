package server.comm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GeneralComm implements Closeable
{
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private BufferedReader createReader()
    {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private BufferedWriter createWriter()
    {
        try {
            return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public GeneralComm(String ip, int port)
    {
        try {
            this.socket = new Socket(ip,port);
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public GeneralComm(ServerSocket server)
    {
        try {
            this.socket = server.accept();
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeLine (String message) throws IOException {
            writer.write(message);
            writer.newLine();
            writer.flush();
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
