package io.github.nichetoolkit.socket;

import io.github.nichetoolkit.socket.server.EnableSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSocketServer
public class SocketToolkitTestWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketToolkitTestWebApplication.class, args);
    }

}
