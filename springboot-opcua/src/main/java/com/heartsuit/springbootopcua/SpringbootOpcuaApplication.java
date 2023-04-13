package com.heartsuit.springbootopcua;

import com.heartsuit.springbootopcua.run.OpcUaStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootOpcuaApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringbootOpcuaApplication.class, args);
        OpcUaStart opcUa = new OpcUaStart();
        opcUa.start();
    }
}
