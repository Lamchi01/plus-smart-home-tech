package ru.yandex.practucum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.WarehouseOperations;

@SpringBootApplication
@EnableFeignClients(basePackageClasses = WarehouseOperations.class)
@EnableDiscoveryClient
public class ShoppingCart {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCart.class, args);
    }
}