package football.league.raveendra.footballleague.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Leagues {

  @JsonProperty("country_id")

  private int countryId;

  @JsonProperty("country_name")
  private String countryName;

  @JsonProperty("league_name")
  private String leagueName;

  @JsonProperty("league_id")
  private int leagueId;

}
