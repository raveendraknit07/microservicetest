package football.league.raveendra.footballleague.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamStandingRequest {

  @NotBlank
  private String teamName;
  @NotBlank
  private String countryName;
  @NotBlank
  private String leagueName;
}
