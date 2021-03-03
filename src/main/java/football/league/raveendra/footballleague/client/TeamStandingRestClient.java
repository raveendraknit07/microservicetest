package football.league.raveendra.footballleague.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import football.league.raveendra.footballleague.log.annotation.Trace;
import football.league.raveendra.footballleague.log.eventtype.LogEventType;
import football.league.raveendra.footballleague.model.Country;
import football.league.raveendra.footballleague.model.Leagues;
import football.league.raveendra.footballleague.model.TeamStanding;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TeamStandingRestClient {

  private static final String APIKEY = "APIkey";
  private final RestTemplate restTemplate;

  @Value("${football.league.base.url}")
  private String baseUrl;

  @Value("${football.league.action.standings}")
  private String standingsAction;

  @Value("${football.league.action.countries}")
  private String countriesAction;

  @Value("${football.league.action.leagues}")
  private String leaguesAction;

  @Value("${football.league.api}")
  private String api;

  @Autowired
  public TeamStandingRestClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @HystrixCommand(fallbackMethod = "getCountries_Fallback",  commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
  @Trace(type = LogEventType.REST_CLIENT)
  public Country[] getCountries() {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("action", countriesAction);
    UriComponentsBuilder builder = getUriComponentsBuilder(baseUrl, queryParams);
    return this.restTemplate
        .exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(getHeaders()),
            Country[].class).getBody();
  }

  private Country[] getCountries_Fallback() {
    return new Country[]{new Country()};
  }

  @HystrixCommand(fallbackMethod = "getLeagues_Fallback",  commandProperties = {
      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
  @Trace(type = LogEventType.REST_CLIENT)
  public Leagues[] getLeagues(int countryId) {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("action", leaguesAction);
    queryParams.put("country_id", String.valueOf(countryId));
    UriComponentsBuilder builder = getUriComponentsBuilder(baseUrl, queryParams);
    return this.restTemplate
        .exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(getHeaders()),
            Leagues[].class).getBody();
  }

  private Leagues[] getLeagues_Fallback(int countryId) {
    Leagues leagues = new Leagues();
    leagues.setCountryId(countryId);
    return new Leagues[]{leagues};
  }

  @HystrixCommand(fallbackMethod = "getTeamStanding_Fallback",  commandProperties = {
      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
  @Trace(type = LogEventType.REST_CLIENT)
  public TeamStanding[] getTeamStanding(int leagueId) {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("action", standingsAction);
    queryParams.put("league_id", String.valueOf(leagueId));
    UriComponentsBuilder builder = getUriComponentsBuilder(baseUrl, queryParams);
    return this.restTemplate
        .exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(getHeaders()),
            TeamStanding[].class).getBody();
  }

  private TeamStanding[] getTeamStanding_Fallback(int leagueId) {
    TeamStanding teamStanding = new TeamStanding();
    teamStanding.setLeagueId(leagueId);
    return new TeamStanding[]{teamStanding};
  }

  private UriComponentsBuilder getUriComponentsBuilder(String url,
      Map<String, String> queryParams) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam(APIKEY, api);
    queryParams.forEach(builder::queryParam);
    return builder;
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }
}
