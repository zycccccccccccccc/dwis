package tech.hciot.dwis.business.infrastructure.integration;


import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;
import tech.hciot.dwis.base.exception.ErrorResponse;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.infrastructure.integration.authenticator.IntegrationAuthenticator;

@Service
public class IntegrationAuthenticationFilter extends GenericFilterBean implements ApplicationContextAware {

  private static final String AUTH_TYPE_PARAM_NAME = "auth_type";

  private static final String OAUTH_TOKEN_URL = "/oauth/token";

  private Collection<IntegrationAuthenticator> authenticators;

  private ApplicationContext applicationContext;

  private RequestMatcher requestMatcher;

  public IntegrationAuthenticationFilter() {
    this.requestMatcher = new OrRequestMatcher(
        new AntPathRequestMatcher(OAUTH_TOKEN_URL, "GET"),
        new AntPathRequestMatcher(OAUTH_TOKEN_URL, "POST")
    );
  }

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    if (requestMatcher.matches(request)) {
      //设置集成登录信息
      IntegrationAuthentication integrationAuthentication = new IntegrationAuthentication();
      integrationAuthentication.setAuthType(request.getParameter(AUTH_TYPE_PARAM_NAME));
      integrationAuthentication.setAuthParameters(request.getParameterMap());
      IntegrationAuthenticationContext.set(integrationAuthentication);
      try {
        //预处理
        this.prepare(integrationAuthentication);

        filterChain.doFilter(request, response);

        //后置处理
        this.complete(integrationAuthentication);
      } catch (Exception e) {
        if (e instanceof PlatformException) {
          response.setCharacterEncoding("UTF-8");
          response.setContentType("application/json; charset=utf-8");
          PlatformException platformException = (PlatformException) e;
          response.setStatus(platformException.getHttpStatus().value());
          ErrorResponse errorResponse = new ErrorResponse();
          errorResponse.setPlatformException(platformException);
          response.getWriter().write(JSONObject.toJSONString(errorResponse));
        }
      } finally {
        IntegrationAuthenticationContext.clear();
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  /**
   * 进行预处理
   */
  private void prepare(IntegrationAuthentication integrationAuthentication) {

    //延迟加载认证器
    if (this.authenticators == null) {
      synchronized (this) {
        Map<String, IntegrationAuthenticator> integrationAuthenticatorMap = applicationContext
            .getBeansOfType(IntegrationAuthenticator.class);
        if (integrationAuthenticatorMap != null) {
          this.authenticators = integrationAuthenticatorMap.values();
        }
      }
    }

    if (this.authenticators == null) {
      this.authenticators = new ArrayList<>();
    }

    for (IntegrationAuthenticator authenticator : authenticators) {
      if (authenticator.support(integrationAuthentication)) {
        authenticator.prepare(integrationAuthentication);
      }
    }
  }

  /**
   * 后置处理
   */
  private void complete(IntegrationAuthentication integrationAuthentication) {
    for (IntegrationAuthenticator authenticator : authenticators) {
      if (authenticator.support(integrationAuthentication)) {
        authenticator.complete(integrationAuthentication);
      }
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
