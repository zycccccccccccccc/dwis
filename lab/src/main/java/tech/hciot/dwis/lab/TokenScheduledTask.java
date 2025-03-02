package tech.hciot.dwis.lab;

import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenScheduledTask {

  public final static long REFRESH_PERIOD = 60 * 1000 * 60 * 20;

  @Autowired
  private DwisBusinessRemoteService dwisBusinessRemoteService;

  @Value("${service-call.clientId}")
  private String clientId;

  @Value("${service-call.clientSecret}")
  private String clientSecret;

  @Scheduled(fixedDelay = REFRESH_PERIOD)
  public void reloadApiToken() {
    String token = this.getToken();
    while (StringUtils.isBlank(token)) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
      }
      token = this.getToken();
    }
    log.info("get service_call.token success,{}", token);
    System.setProperty("service_call.token", token);
    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.info("@@@@@@@@@@@@@@@    OK  OK  OK    @@@@@@@@@@@@@@@");
    log.info("@@@@@@@@@@@@@@@    OK  OK  OK    @@@@@@@@@@@@@@@");
    log.info("@@@@@@@@@@@@@@@    OK  OK  OK    @@@@@@@@@@@@@@@");
    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
  }

  public String getToken() {
    Map<String, String> params = new HashMap<>();
    params.put("grant_type", "client_credentials");
    params.put("client_id", clientId);
    params.put("client_secret", clientSecret);
    JSONObject jsonObject = null;
    try {
      jsonObject = dwisBusinessRemoteService.getToken(params);
    } catch (Exception e) {
      log.error("oauth2 token service error");
    }
    return Optional.ofNullable(jsonObject).map(json -> json.getString("access_token")).orElse("");
  }
}
