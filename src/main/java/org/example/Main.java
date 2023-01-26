package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    //Системные сообщения
    private static final String ERRORMSG = "Что-то пошло не так :(";
    private static final String EXITMSG = "#STOP#";

    //Настройки
    private static final String NAMEOFSETTINGSFILE = "settings.json"; //Имя файла настроек

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Settings settings = Settings.getInstance(NAMEOFSETTINGSFILE); //Читаем файл настроек и создаем объект настроек
        Logger logger = Logger.getInstance(settings); //Создаем объект логгера
        Server server;
        try {
            server = new Server(settings.getServerPort(), logger);
        } catch (IOException exc) {
            logger.log(ERRORMSG);
            throw new RuntimeException(exc);
        }
        while (true) {
            String message = scanner.nextLine();
            if (message.equals(EXITMSG)) {
                break;
            }
        }
        server.interrupt();
    }
}