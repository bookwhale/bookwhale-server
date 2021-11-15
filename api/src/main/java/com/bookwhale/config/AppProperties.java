package com.bookwhale.config;

import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final Auth auth;
  private final OAuth2 oauth2;
  private final NaverBook naverBook;

  public AppProperties(Auth auth, OAuth2 oauth2, NaverBook naverBook) {
    this.auth = auth;
    this.oauth2 = oauth2;
    this.naverBook = naverBook;
  }

  @Getter
  public static class Auth {

    private final String tokenSecret;
    private final long tokenExpirationMsec;

    public Auth(String tokenSecret, long tokenExpirationMsec) {
      this.tokenSecret = tokenSecret;
      this.tokenExpirationMsec = tokenExpirationMsec;
    }
  }

  @Getter
  public static final class OAuth2 {

    private final List<String> authorizedRedirectUris;

    public OAuth2(List<String> authorizedRedirectUris) {
      this.authorizedRedirectUris = authorizedRedirectUris;
    }
  }

  @Getter
  public static final class NaverBook {

    private final String clientId;
    private final String clientSecret;

    public NaverBook(String clientId, String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
    }
  }
}
