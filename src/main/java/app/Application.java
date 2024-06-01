package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Tickets;

import java.io.File;
import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Tickets tickets = objectMapper.readValue(new File("src/main/resources/tickets.json"), Tickets.class);

            System.out.println(tickets);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}