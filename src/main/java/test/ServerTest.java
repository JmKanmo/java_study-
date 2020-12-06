package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            ServerSocket serverSocket = null;

            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(5001);
                    System.out.println("서버 오픈!!");

                    while (true) {
                        Socket socket = serverSocket.accept();
                        System.out.println(socket.toString());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
                                        socket.getInputStream());) {
                                    while (true) {
                                        try {
                                            // TODO Auto-generated method stub
                                            byte[] byteArr = new byte[100];
                                            int inputCount = bufferedInputStream.read(byteArr);
                                            if (inputCount == -1) {
                                                throw new IOException();
                                            }
                                            String data = new String(byteArr, 0, inputCount, "UTF-8");
                                            System.out.println("클라이언트로부터 " + data + "를 전달받았습니다.");
                                        } catch (Exception e) {
                                            System.out.println("클라이언트와 연결이 끊겼습니다.");
                                            try {
                                                if (socket.isClosed() != true) {
                                                    socket.close();
                                                }
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                        socket.getOutputStream());) {
                                    while (true) {
                                        try {
                                            String data = "바보멍청이";
                                            byte[] byteArr = data.getBytes("UTF-8");
                                            bufferedOutputStream.write(byteArr);
                                            bufferedOutputStream.flush();
                                            System.out.println("서버가 클라이언트에게 " + data + "를 전달했습니다.");
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (IOException e) {
                                            System.out.println("클라이언트와 연결이 끊겼습니다.");
                                            try {
                                                if (socket.isClosed() != true) {
                                                    socket.close();
                                                }
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("바인딩포트가 실패했습니다.");
                    try {
                        serverSocket.close();
                        System.out.println("서버소켓을 닫습니다.");
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
