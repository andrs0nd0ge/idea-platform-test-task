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
import java.util.*;
import java.util.stream.Collectors;

public class Application {
    private final String origin = "VVO";
    private final String destination = "TLV";
    @SuppressWarnings("FieldCanBeLocal")
    private final String jsonPath = "src/main/resources/tickets.json";
    @SuppressWarnings("FieldCanBeLocal")
    private final String dateFormat = "dd.MM.yy";
    @SuppressWarnings("FieldCanBeLocal")
    private final String timeFormat = "H:mm";
    public static void main(String[] args) {
        Application application = new Application();
        application.executeFirstTask();
        application.executeSecondTask();
    }

    private void executeFirstTask() {
        try {
            List<Ticket> tickets = getTickets();

            Map<String, List<Ticket>> map = tickets.stream()
                    .collect(Collectors.groupingBy(Ticket::getCarrier));

            for (Map.Entry<String, List<Ticket>> entry : map.entrySet()) {
                String carrier = entry.getKey();
                List<Ticket> ticketList = entry.getValue();

                Optional<Ticket> ticketOptional = ticketList.stream()
                        .min((t1, t2) -> {
                            Duration d1 = getDuration(t1);
                            Duration d2 = getDuration(t2);

                            return d1.compareTo(d2);
                        });

                if (ticketOptional.isPresent()) {
                    Duration duration = getDuration(ticketOptional.get());

                    System.out.println(carrier + ": " + duration.toMinutes() + " minutes");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private List<Ticket> getTickets() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Tickets tickets = objectMapper.readValue(new File(jsonPath), Tickets.class);

        List<Ticket> ticketList = tickets.getTickets();

        return ticketList.stream()
                .filter(e -> e.getOrigin().equals(origin) && e.getDestination().equals(destination))
                .collect(Collectors.toList());
    }

    private Duration getDuration(Ticket ticket) {
        LocalDate arrivalDate = LocalDate.parse(ticket.getArrivalDate(), DateTimeFormatter.ofPattern(dateFormat));
        LocalTime arrivalTime = LocalTime.parse(ticket.getArrivalTime(), DateTimeFormatter.ofPattern(timeFormat));
        LocalDate departureDate = LocalDate.parse(ticket.getDepartureDate(), DateTimeFormatter.ofPattern(dateFormat));
        LocalTime departureTime = LocalTime.parse(ticket.getDepartureTime(), DateTimeFormatter.ofPattern(timeFormat));

        LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime);
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);

        return Duration.between(departureDateTime, arrivalDateTime);
    }

    private void executeSecondTask() {
        try {
            List<Ticket> tickets = getTickets();

            List<Double> prices = tickets.stream()
                    .sorted(Comparator.comparing(Ticket::getPrice))
                    .map(Ticket::getPrice)
                    .collect(Collectors.toList());

            double median = calculateMedianOf(prices);

            OptionalDouble optionalAveragePrice = tickets.stream()
                    .mapToDouble(Ticket::getPrice)
                    .average();

            if (optionalAveragePrice.isPresent()) {
                double averagePrice = optionalAveragePrice.getAsDouble();

                double doubleResult = Math.abs(averagePrice - median);

                printSecondTaskResult(doubleResult);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private double calculateMedianOf(List<Double> pricesList) {
        int ticketCount = pricesList.size();

        boolean pricesAmountIsEven = ticketCount % 2 == 0;

        double median;

        if (!pricesAmountIsEven) {
            int middleIndex = (ticketCount - 1) / 2;

            median = pricesList.get(middleIndex);

            return median;
        } else {
            int firstIndex = (int) Math.floor((ticketCount - 1) / 2.0);

            int secondIndex = (int) Math.ceil((ticketCount - 1) / 2.0);

            median = (pricesList.get(firstIndex) + pricesList.get(secondIndex)) / 2;

            return median;
        }
    }

    private void printSecondTaskResult(double doubleResult) {
        try {
            int result = (int) doubleResult;
            System.out.println(result);
        } catch (ClassCastException ex) {
            System.out.println(doubleResult);
        }
    }
}