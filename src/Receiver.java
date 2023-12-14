import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Receiver {
    public static int port = 9876;
    public static String savePath = "file/ReceiverFile/11111.md"; // 替换为你想保存文件的路径

    // 建立连接

    // 1-接受连接请求
    public static DatagramPacket receiveConnectRequest(DatagramSocket socket) throws Exception{
        byte[] buffer = new byte[1024];
        DatagramPacket connectPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(connectPacket);
        System.out.println("已收到CONNECT");
        return connectPacket;
    }

    // 2-发送连接确认
    public static void sendConnectConfirm(DatagramSocket socket,InetAddress senderAddress) throws Exception{
        String acknowledgmentMessage = "ACK";
        DatagramPacket acknowledgmentPacket = new DatagramPacket(acknowledgmentMessage.getBytes(), acknowledgmentMessage.length(), senderAddress , port);
        socket.send(acknowledgmentPacket);
        System.out.println("已发送ACK");
    }

    // 3-接收对连接确认的确认
    public static boolean receiveACKConfirmRequest(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket ackConfirmPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(ackConfirmPacket);
        String ackConfirmMessage = new String(ackConfirmPacket.getData(), 0, ackConfirmPacket.getLength());
        if(ackConfirmMessage.equals("ACK_CONFIRM")){
            System.out.println("已收到ACK_CONFIRM");
        }
        return ackConfirmMessage.equals("ACK_CONFIRM");
    }

    // 接收文件
    public static void receiveFile(DatagramSocket socket) throws Exception{
            byte[] buffer = new byte[1024 * 64]; // 根据实际情况调整缓冲区大小

            DatagramPacket filePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(filePacket);

            try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(savePath)))) {
                bos.write(buffer, 0, filePacket.getLength());
            }

            System.out.println("文件接收成功并保存在" + savePath);
    }
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            // 等待连接请求
            DatagramPacket connectPacket = receiveConnectRequest(socket);

            // 发送连接确认
            sendConnectConfirm(socket, connectPacket.getAddress());

            if(receiveACKConfirmRequest(socket)) {
                // 接收文件
                receiveFile(socket);
                System.out.println("文件接收成功并保存在" + savePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
