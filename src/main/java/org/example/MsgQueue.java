package org.example;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MsgQueue extends Thread {

    private Queue<String> msgQueue; //Объявляем переменную для очереди сообщений
    private List<Connection> connectionList; //Объявляем переменную для списка соединений

    public MsgQueue(List<Connection> connectionList) {
        this.msgQueue = new ConcurrentLinkedQueue();
        this.connectionList = connectionList;
        start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if (!msgQueue.isEmpty()) { //Проверяем не пуста ли очередь сообщений
                String message = msgQueue.poll();
                for (Connection connection : connectionList) {
                    if (!connection.getSocket().isClosed()) { //Проверяем жив ли клиент
                        connection.send(message);
                    } else {
                        connectionList.remove(connection);
                    }
                }
            }
        }
    }

    public void addMessageToQueue(String message) {
        msgQueue.add(message);
    }
    
}
