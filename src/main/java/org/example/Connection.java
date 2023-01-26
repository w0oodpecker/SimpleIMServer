package org.example;

import java.io.*;
import java.net.Socket;

public class Connection extends Thread {

    //Системные сообщения
    private static final String EXITMSG = "/exit";
    private static final String RECIEVEDMSG = "[Получен]";
    private static final String SENTDMSG = "[Отправлен]";
    private static final String LOSTCONNECTIONMSG = "Соединение разорвано";
    private static final String NEWCONNECTIONMSG = "Соединение установлено";
    private static final String EXITCLIENTMSG = "Покинул чат";


    private Socket socket; //сокет соединения
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private MsgQueue msgQueue; //общая очередь сообщений
    private Logger logger; //Объект логгера


    public Connection(Socket socket, MsgQueue msgQueue, Logger logger) throws IOException {
        this.logger = logger;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Создаем входящий поток
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //Создаем исходящий поток
        this.msgQueue = msgQueue;
        this.start();
        logger.log(NEWCONNECTIONMSG);
    }


    @Override
    public void run() {
        String message = null;
        try {
            while (!isInterrupted()) {
                if (!socket.isClosed()) { //Проверяем есть ли соединение
                    message = this.in.readLine();
                    if (message.contains(EXITMSG)) { //Проверяем сигнал отклиента на завершение сессии
                        this.msgQueue.addMessageToQueue(message + " " + EXITCLIENTMSG);
                        logger.log(message + " " + EXITCLIENTMSG);
                        break;
                    }
                    this.msgQueue.addMessageToQueue(message);
                    logger.log(RECIEVEDMSG + message);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
        } finally {
            this.close(); //Закрываем соединение
        }
    }


    public void send(String message) {
        try {
            this.out.write(message + "\n");
            this.out.flush();
            logger.log(SENTDMSG + message);
        } catch (IOException ignored) {
        }
    }


    public Socket getSocket() {
        return socket;
    }


    public void close() {
        if (!socket.isClosed()) {
            try {
                this.socket.shutdownInput(); //Закрываем сокет
                this.socket.shutdownOutput();
                this.socket.close();
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }
        try {
            this.in.close(); //Закрываем входящий поток
            this.out.close(); //Закрываем исходящий поток
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        logger.log(LOSTCONNECTIONMSG);
    }
}
