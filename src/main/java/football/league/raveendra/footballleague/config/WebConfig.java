package football.league.raveendra.footballleague.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

  @Value("${rest.client.read.timeout}")
  private int readTimeout;
  @Value("${rest.client.connect.timeout}")
  private int connectTimeout;


  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate(getClientHttpRequestFactory());
  }

  private ClientHttpRequestFactory getClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
        new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setReadTimeout(readTimeout);
    clientHttpRequestFactory.setConnectTimeout(connectTimeout);
    return clientHttpRequestFactory;
  }
}
