package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.aggregator.AggregationStarter;

@SpringBootApplication
public class CollectorApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CollectorApp.class, args);
        AggregationStarter aggregator = context.getBean(AggregationStarter.class);
        aggregator.start();
    }
}