package com.maximilian.restaurant.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.maximilian.restaurant.client")
@SpringBootApplication(scanBasePackages = {
        "com.maximilian.restaurant.amqp",
        "com.maximilian.restaurant.order"
})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
