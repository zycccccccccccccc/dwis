package tech.hciot.dwis.business.application;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tech.hciot.dwis.base.dto.AccountRequest;
import tech.hciot.dwis.base.dto.ResetPasswordRequest;
import tech.hciot.dwis.business.infrastructure.FeignConfiguration;

@FeignClient(name = "dwis-business", path = "/dwis", configuration = FeignConfiguration.class)
public interface SmCenterRemoteService {

  @RequestMapping(value = "/oauth/resetpassword", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  void resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest);

  @RequestMapping(value = "/oauth/user/{username}/role", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  List<String> getUserRoles(@PathVariable("username") String username);

  @RequestMapping(value = "/oauth/token", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  JSONObject getToken(Map<String, ?> entityBody);

  @RequestMapping(value = "/oauth/account", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  void createAccount(@RequestBody AccountRequest accountRequest);

  @RequestMapping(value = "/oauth/account/{username}", method = RequestMethod.DELETE)
  void deleteAccount(@PathVariable("username") String username);

}
