package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ThreadTest {
    private static Socket socket;

    public static void main(String[] args) {
        try {
            // 클라이언트 ㄱㄱ
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 5001));
            System.out.println("클라이언트정보:" + socket.toString());
        } catch (Exception e) {
            System.out.println("서버와의 연결 실패");
            try {
                if (socket.isClosed() != true) {
                    socket.close();
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 서버 ㄱㄱ
                BufferedOutputStream bufferedOutputStream = null;
                while (true) {
                    try {
                        bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                        String data = "안녕 내사랑";
                        bufferedOutputStream.write(data.getBytes("UTF-8"));
                        bufferedOutputStream.flush();
                        System.out.println("서버에게 " + data + "를 전달했습니다.");
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        System.out.println("서버와의 연결이 끊겼습니다.");
                        try {
                            bufferedOutputStream.close();
                            socket.close();
                            System.out.println("서버와의 통신을 중단합니다.");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }).start();
    }
}
