package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Server {
    public static void main(String[] args) {
        ArrayList<Socket> clinetSokets = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // Создаёи серверный сокет
            System.out.println("Сервер запущен");
            while (true) { // бесконечный цикл для ожидания подключения клиентов
                System.out.println("Ожидаю подключения клиентов...");
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                clinetSokets.add(socket);
                System.out.println("Клиент подключился");
                DataInputStream in = new DataInputStream(socket.getInputStream()); // Поток ввода
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Поток вывода
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String request = null;
                        while (true) {
                            try {
                                request = in.readUTF(); // Принимает сообщение от клиента
                                System.out.println("Клиент прислал: " + request);
                                for (Socket clinetSoket : clinetSokets) { // Перебираем клиентов которые подключенны в настоящий момент
                                    DataOutputStream out = new DataOutputStream(clinetSoket.getOutputStream());
                                    out.writeUTF(request.toUpperCase(Locale.ROOT)); // Рассылает принятое сообщение всем клиентам
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                clinetSokets.remove(socket); // Удаление сокета, когда клиент отключился
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