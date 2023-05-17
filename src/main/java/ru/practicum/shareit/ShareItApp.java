package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
Привет! Кавычки были нужны потому что в SQL слово user зарезервировано.
Не подскажешь почему при получении запроса по эндпоинту items/{id} приложение отвечает намного дольше чем по другим?
*/
@SpringBootApplication
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}
