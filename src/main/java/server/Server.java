package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Server {
    public static void main(String[] args) {
        ArrayList<Socket> clientSockets = new ArrayList<>();
        Map<Socket, String> clientNames = new HashMap<>();
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // Создаёи серверный сокет
            System.out.println("Сервер запущен");
            while (true) { // бесконечный цикл для ожидания подключения клиентов
                System.out.println("Ожидаю подключения клиентов...");
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента

                clientSockets.add(socket);
                clientNames.put(socket,"Nameless");

                System.out.println("Клиент подключился");
                DataInputStream in = new DataInputStream(socket.getInputStream()); // Поток ввода
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Поток вывода



                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String request = null;
                        while (true) {
                            try {
                                if(clientNames.get(socket).equals("Nameless")) {
                                    out.writeUTF("Привет " + clientNames.get(socket));
                                    out.writeUTF("Как тебя величать? ->");
                                    clientNames.put(socket, in.readUTF());
                                    out.writeUTF("Hello " + clientNames.get(socket));
                                }

                                request = in.readUTF(); // Принимает сообщение от клиента
                                System.out.println("Клиент прислал: " + request);
                                for (Socket clientSocket : clientSockets) { // Перебираем клиентов которые подключенны в настоящий момент
                                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                                    if(!socket.equals(clientSocket)) {
                                        out.writeUTF("Получено сообщение от пользователя: " + clientNames.get(socket));
                                        out.writeUTF(request.toUpperCase(Locale.ROOT)); // Рассылает принятое сообщение всем клиентам
                                    }
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                clientSockets.remove(socket); // Удаление сокета, когда клиент отключился
                                break;
                            }
                        }
                    }
                });
                thread.start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}