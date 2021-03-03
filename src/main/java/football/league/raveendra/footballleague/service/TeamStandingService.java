package football.league.raveendra.footballleague.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import football.league.raveendra.footballleague.client.TeamStandingRestClient;
import football.league.raveendra.footballleague.dto.TeamStandingDto;
import football.league.raveendra.footballleague.exception.BadRequestException;
import football.league.raveendra.footballleague.log.annotation.Trace;
import football.league.raveendra.footballleague.log.eventtype.LogEventType;
import football.league.raveendra.footballleague.model.Country;
import football.league.raveendra.footballleague.model.Leagues;
import football.league.raveendra.footballleague.model.TeamStanding;
import football.league.raveendra.footballleague.model.TeamStandingRequest;

@Service
@Slf4j
public class TeamStandingService {

  private final TeamStandingRestClient teamStandingRestClient;

  @Autowired
  public TeamStandingService(TeamStandingRestClient teamStandingRestClient) {
    this.teamStandingRestClient = teamStandingRestClient;
  }

  @Trace(type = LogEventType.SERVICE)
  public TeamStandingDto getTeamStanding(TeamStandingRequest teamStandingRequest) {
    //Validate the request
    TeamStanding teamStandingDefault = getDefaultTeamStanding(teamStandingRequest);
    List<Country> countries = getCountries();
    Country country = getCountryByName(teamStandingRequest, countries);
    if (!isValidateCountryResponse(teamStandingRequest, teamStandingDefault, country)) {
      return TeamStandingDto.from(teamStandingDefault);
    }
    teamStandingDefault.setCountryId(country.getId());

    List<Leagues> leaguesList = getLeagues(country.getId());
    Leagues leagues = getLeaguesByName(teamStandingRequest, leaguesList);
    if (!isValidLeagueResponse(teamStandingRequest, teamStandingDefault, leagues)) {
      return(TeamStandingDto.from(teamStandingDefault));
    }
    teamStandingDefault.setLeagueId(leagues.getLeagueId());
    List<TeamStanding> teamStandings = getTeamStanding(leagues.getLeagueId());
    log.info("team standing found {}", teamStandings.toString());

    TeamStanding teamStandingsFiltered = getFilteredTeamStanding(teamStandingRequest,
        teamStandings);
    teamStandingsFiltered.setCountryId(country.getId());
    log.info("team standing filtered found {}", teamStandingsFiltered.toString());
    if (teamStandingsFiltered.getTeamId()==0){
      return TeamStandingDto.from(teamStandingDefault);
    }

    return TeamStandingDto.from(teamStandingsFiltered);
  }

  private Country getCountryByName(TeamStandingRequest teamStandingRequest,
      List<Country> countries) {
    return countries.stream()
        .filter(c -> teamStandingRequest.getCountryName().equalsIgnoreCase(c.getName())).findFirst()
        .orElse(null);
  }

  private Leagues getLeaguesByName(TeamStandingRequest teamStandingRequest,
      List<Leagues> leaguesList) {
    return leaguesList.stream()
        .filter(l -> teamStandingRequest.getLeagueName().equalsIgnoreCase(l.getLeagueName()))
        .findFirst().orElse(null);
  }

  private TeamStanding getFilteredTeamStanding(TeamStandingRequest teamStandingRequest,
      List<TeamStanding> teamStandings) {
    return teamStandings.stream()
        .filter(t -> teamStandingRequest.getTeamName().equalsIgnoreCase(t.getTeamName()))
        .findFirst().orElse(null);
  }

  private boolean isValidLeagueResponse(TeamStandingRequest teamStandingRequest,
      TeamStanding teamStandingDefault, Leagues leagues) {
    if (Objects.isNull(leagues)) {
      throw new BadRequestException(
          "leagues Not Found by name " + teamStandingRequest.getLeagueName());
    }
    log.info("league found {}", leagues.toString());
    if (leagues.getLeagueId() == 0) {
      return false;
    }
    return true;
  }

  private boolean isValidateCountryResponse(TeamStandingRequest teamStandingRequest,
      TeamStanding teamStandingDefault, Country country) {
    if (Objects.isNull(country)) {
      throw new BadRequestException(
          "Country Not Found by name " + teamStandingRequest.getCountryName());
    }
    log.info("Country found {}", country.toString());

    if (country.getId() == 0) {
      teamStandingDefault.setCountryId(0);
      return false;
    }
    return true;
  }

  private TeamStanding getDefaultTeamStanding(TeamStandingRequest teamStandingRequest) {
    TeamStanding teamStanding = new TeamStanding();
    teamStanding.setTeamName(teamStandingRequest.getTeamName());
    teamStanding.setCountryName(teamStandingRequest.getCountryName());
    teamStanding.setLeagueName(teamStandingRequest.getLeagueName());
    return teamStanding;
  }

  private List<Country> getCountries() {
    return new ArrayList<>(Arrays.asList(teamStandingRestClient.getCountries()));
  }

  private List<Leagues> getLeagues(int countryId) {
    return new ArrayList<>(Arrays.asList(teamStandingRestClient.getLeagues(countryId)));
  }


  private List<TeamStanding> getTeamStanding(int leagueId) {
    return new ArrayList<>(Arrays.asList(teamStandingRestClient.getTeamStanding(leagueId)));
  }

}
