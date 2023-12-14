import java.io.*;
import java.net.*;

public class Receiver {
    public static void main(String[] args) {
        int port = 9876;
        String savePath = "file/ReceiverFile/11111.md"; // 替换为你想保存文件的路径

        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[1024 * 64]; // 根据实际情况调整缓冲区大小

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savePath))) {
                bos.write(buffer, 0, packet.getLength());
            }

            System.out.println("File received and saved at: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
