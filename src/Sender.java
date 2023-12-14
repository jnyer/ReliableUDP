import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class Sender {
    public static String filePath = "file/SenderFile/11111.md";
    public static String host = "127.0.0.1";
    public static int port = 9876;

    // 建立连接

    // 1-发送连接请求
    public static void sendConnectRequest(DatagramSocket socket, InetAddress receiverAddress) throws Exception{
        String connectMessage = "CONNECT";
        DatagramPacket connectPacket = new DatagramPacket(connectMessage.getBytes(),connectMessage.length(),receiverAddress,port);
        socket.send(connectPacket);
        System.out.println("已发送CONNECT");
    }

    // 2-收到确认消息
    public static boolean receiveConnectConfirm(DatagramSocket socket) throws Exception{
        byte[] acknowledgmentBuffer = new byte[1024];
        DatagramPacket acknowledgmentPacket = new DatagramPacket(acknowledgmentBuffer, acknowledgmentBuffer.length);
        socket.receive(acknowledgmentPacket);

        String acknowledgmentMessage = new String(acknowledgmentPacket.getData(), 0, acknowledgmentPacket.getLength());
        if(acknowledgmentMessage.equals("ACK")) {
            System.out.println("已收到ACK");
        }
        return acknowledgmentMessage.equals("ACK");
    }

    // 3-收到确认消息后发送应答报文，完成三次握手
    public static boolean sendACKConfirmRequest(DatagramSocket socket, InetAddress receiverAddress) throws Exception{
        String ackConfirmMessage = "ACK_CONFIRM";
        DatagramPacket ackConfirmPacket = new DatagramPacket(ackConfirmMessage.getBytes(),ackConfirmMessage.length(),receiverAddress,port);
        socket.send(ackConfirmPacket);
        System.out.println("已发送ACK_CONFIRM");
        return true;
    }


    // 发送文件
    public static void sendFile(DatagramSocket socket, InetAddress receiverAddress) throws Exception{
            File file = new File(filePath);
            byte[] buffer = new byte[(int) file.length()];

            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
                bis.read(buffer, 0, buffer.length);
            }

            DatagramPacket filePacket = new DatagramPacket(buffer, buffer.length, receiverAddress, port);
            socket.send(filePacket);

            System.out.println("文件发送成功");
    }

    public static void main(String[] args) {
        try(DatagramSocket socket = new DatagramSocket()){
            InetAddress receiverAddress = InetAddress.getByName(host);

            // 发送连接请求
            sendConnectRequest(socket, receiverAddress);

            // 接收连接确认
            if (receiveConnectConfirm(socket)) {
                // 发送第三次握手确认消息
                if(sendACKConfirmRequest(socket, receiverAddress)) {
                    // 发送文件
                    sendFile(socket, receiverAddress);
                    System.out.println("文件发送成功");
                }
            } else {
                System.out.println("连接建立失败，已退出");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}