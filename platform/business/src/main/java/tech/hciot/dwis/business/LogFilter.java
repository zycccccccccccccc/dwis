package tech.hciot.dwis.business;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@WebFilter(value = "/*", filterName = "logFilter")
@Slf4j
public class LogFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (StringUtils.contains(request.getContentType(), "multipart")) {
      chain.doFilter(request, response);
    } else {
      try {
        WrappedHttpServletRequest requestWrapper =
            new WrappedHttpServletRequest((HttpServletRequest) request);

        // 获取请求参数
        String requestBody = requestWrapper.getRequestParams();
        if (!StringUtils.isBlank(requestBody)) {
          if (requestBody.length() >= 8192) {
            requestBody = requestBody.substring(0, 8192);
          }
          request.setAttribute("request-body", requestBody);
        }

        // 这里doFilter传入我们实现的子类
        chain.doFilter(requestWrapper, response);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  @Override
  public void destroy() {
  }
}
