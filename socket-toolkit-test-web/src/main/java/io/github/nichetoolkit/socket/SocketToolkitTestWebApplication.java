package io.github.nichetoolkit.socket;

import io.github.nichetoolkit.socket.server.EnableSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;

@EnableSocketServer
@SpringBootApplication(scanBasePackages = "io.github.nichetoolkit")
public class SocketToolkitTestWebApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SocketToolkitTestWebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SocketToolkitTestWebApplication.class);
    }
}
