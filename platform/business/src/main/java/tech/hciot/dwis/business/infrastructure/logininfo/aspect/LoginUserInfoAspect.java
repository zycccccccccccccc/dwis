package tech.hciot.dwis.business.infrastructure.logininfo.aspect;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.business.infrastructure.logininfo.domain.LoginUserInfoRepository;
import tech.hciot.dwis.business.infrastructure.logininfo.domain.model.LoginUserInfo;

@Component
@Aspect
@Slf4j
public class LoginUserInfoAspect {

  private static DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private LoginUserInfoRepository loginUserInfoRepository;

  @Pointcut("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
  private void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String username = getUsername();
    if (isOauthInfo(point)
      || isLogout(point)
      || "anonymousUser".equals(username)
      || "admin".equals(username)
      || "services".equals(username)) {
      return point.proceed();
    }

    String ip = getRemoteIp(request);
    LoginUserInfo loginUserInfo = loginUserInfoRepository.findByUsername(username)
      .orElse(LoginUserInfo.builder().build());
    if (loginUserInfo.getUsername() != null) {
      Date last24 = DateUtils.addHours(new Date(), -24);
      if (!ip.equals(loginUserInfo.getIp()) && loginUserInfo.getLastActiveTime().after(last24)) {
        throw PlatformException.badRequestException("该账户已在 " + ip + " 登录，请检查登录账户是否正确！");
      }
    }
    loginUserInfo.setIp(ip);
    loginUserInfo.setUsername(username);
    loginUserInfo.setLastActiveTime(new Date());
    try {
      loginUserInfoRepository.save(loginUserInfo);
    } catch(Exception e) {
      log.error(e.getMessage());
    }
    return point.proceed();
  }

  private String getRemoteIp(HttpServletRequest request) {
    String remoteIp = request.getHeader("X-Forwarded-For");
    if (StringUtils.isBlank(remoteIp)) {
      remoteIp = request.getRemoteAddr();
    }
    if (StringUtils.isBlank(remoteIp)) {
      remoteIp = "127.0.0.1";
    }
    return remoteIp;
  }

  private String getUsername() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof JwtTokenUser) {
      return ((JwtTokenUser) principal).getUsername();
    } else {
      return principal.toString();
    }
  }

  private Method getMethod(ProceedingJoinPoint point) {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    return method;
  }

  private boolean isOauthInfo(ProceedingJoinPoint point) {
    Method method = getMethod(point);

    GetMapping getMapping = method.getAnnotation(GetMapping.class);
    if (getMapping != null) {
      if ("/oauth/info".equals(getMapping.value()[0])) {
        return true;
      }
    }
    return false;
  }

  private boolean isLogout(ProceedingJoinPoint point) {
    Method method = getMethod(point);

    PostMapping postMapping = method.getAnnotation(PostMapping.class);
    if (postMapping != null) {
      if ("/oauth/logout".equals(postMapping.value()[0])) {
        return true;
      }
    }
    return false;
  }
}
