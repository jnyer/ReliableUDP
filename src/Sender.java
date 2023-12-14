import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class Sender {
    public static void main(String[] args) {
        String filePath = "file/SenderFile/11111.md";
        String host = "127.0.0.1";
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress receiverAddress = InetAddress.getByName(host);
            File file = new File(filePath);
            byte[] buffer = new byte[(int) file.length()];

            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
                bis.read(buffer, 0, buffer.length);
            }

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, port);
            socket.send(packet);

            System.out.println("File sent successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}