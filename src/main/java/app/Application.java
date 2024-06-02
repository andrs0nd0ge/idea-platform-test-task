package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Ticket;
import models.Tickets;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Application {
    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Tickets ticketsObject = objectMapper.readValue(new File("src/main/resources/tickets.json"), Tickets.class);

            List<Ticket> tickets = ticketsObject.getTickets();

            tickets = tickets.stream()
                    .filter(e -> e.getOrigin().equals("VVO") && e.getDestination().equals("TLV"))
                    .collect(Collectors.toList());

            Map<String, List<Ticket>> map = tickets.stream()
                    .collect(Collectors.groupingBy(Ticket::getCarrier));

            for (Map.Entry<String, List<Ticket>> someth : map.entrySet()) {
                List<Ticket> ticketList = someth.getValue();

                for (Ticket ticket : ticketList) {
                    LocalDate arrivalDate = LocalDate.parse(ticket.getArrivalDate(), DateTimeFormatter.ofPattern("dd.MM.yy"));
                    LocalTime arrivalTime = LocalTime.parse(ticket.getArrivalTime(), DateTimeFormatter.ofPattern("H:mm"));
                    LocalDate departureDate = LocalDate.parse(ticket.getDepartureDate(), DateTimeFormatter.ofPattern("dd.MM.yy"));
                    LocalTime departureTime = LocalTime.parse(ticket.getDepartureTime(), DateTimeFormatter.ofPattern("H:mm"));

                    LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime);

                    LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);

                    Duration duration = Duration.between(departureDateTime, arrivalDateTime);

                    System.out.println(duration.toMinutes());
                    System.out.println(ticket.getCarrier() + ":");
                    System.out.println(departureDate + ", " + departureTime);
                    System.out.println(arrivalDate + ", " + arrivalTime + "\n");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}