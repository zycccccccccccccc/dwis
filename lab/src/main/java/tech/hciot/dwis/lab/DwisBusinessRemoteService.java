package tech.hciot.dwis.lab;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import tech.hciot.dwis.lab.domain.model.ChemistryDetail;

@FeignClient(name = "dwis-business", path = "/dwis", url = "${dwis-business.url}", configuration = FeignConfiguration.class)
public interface DwisBusinessRemoteService {

  @PostMapping(value = "oauth/token",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  JSONObject getToken(Map<String, ?> entityBody);

  @PostMapping(value = "lab")
  ChemistryDetail add(ChemistryDetail request);

  @PostMapping(value = "lab/keepalive")
  void keepAlive();
}
