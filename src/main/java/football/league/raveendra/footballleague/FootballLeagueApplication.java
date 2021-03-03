package football.league.raveendra.footballleague;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@EnableCircuitBreaker
public class FootballLeagueApplication {

  public static void main(String[] args) {
    SpringApplication.run(FootballLeagueApplication.class, args);
  }

}
