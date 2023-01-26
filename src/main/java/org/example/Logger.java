package org.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {

    private static Logger instance; //для хранения инстанса логгера
    private FileOutputStream outputStream; //Объявляем переменную для объекта файла журнала
    private SimpleDateFormat formater; //Объявляем переменную для объекта формата даты и время

    private Logger(Settings settings) {
        try {
            outputStream = new FileOutputStream(settings.getGetNameOfLoggerFile(), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        formater = new SimpleDateFormat(settings.getFormatOfDate());
    }

    public void log(String messsage) {
        try {
            outputStream.write((formater.format(new Date().getTime()) + " " + (messsage + "\n")).getBytes());
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void close() {
        try {
            outputStream.close();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static Logger getInstance(Settings settings) {
        if (instance == null) {
            instance = new Logger(settings);
        }
        return instance;
    }
}
