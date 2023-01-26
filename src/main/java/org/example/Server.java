package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Thread {

    private static final String STARTEDSERVERMSG = "Сервер стратован";
    private static final String STOPPEDSERVERMSG = "Сервер остановлен";
    private static final String STOPINGSERVERMSG = "Останавливаем сервер";


    private List<Connection> connectionList; // список соединений
    private ServerSocket server;
    private MsgQueue msgQueue; //Объект очереди сообщений
    private Logger logger;
    private boolean runningServer = true;


    public Server(int serverPort, Logger logger) throws IOException {
        this.logger = logger;
        this.server = new ServerSocket(serverPort);
        this.connectionList = new CopyOnWriteArrayList(); //Содаем потокозащищенный список для регистрации клиентских соединений
        this.msgQueue = new MsgQueue(connectionList); //Создаем объект обработки очереди сообщений
        this.start();
        System.out.println(STARTEDSERVERMSG);
        logger.log(STARTEDSERVERMSG);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                server.setSoTimeout(10000); //Устанавливаем период времени для прерывания ожидания входящего запроса на соединение
                try {
                    Socket socket = server.accept();
                    connectionList.add(new Connection(socket, msgQueue, logger)); // добавить новое соединенние в список
                } catch (SocketTimeoutException exc) {
                }
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } finally {
            this.close(); //Закрываем работу сервера
        }
    }

    public void close() {
        System.out.println(STOPINGSERVERMSG);
        for (Connection connection : connectionList) { //Перебираем все зарегистрированные соединения
            if (!connection.getSocket().isClosed()) {
                connection.close(); //Закрываем открытые соединения клиентов
                connectionList.remove(connection); //Удаляем соединения из списка
            }
        }
        msgQueue.interrupt(); //Завершаем работу очереди сообщений
        logger.log(STOPPEDSERVERMSG);
        logger.close();
        System.out.println("\n" + STOPPEDSERVERMSG);
    }
}
