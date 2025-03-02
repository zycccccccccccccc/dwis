package tech.hciot.dwis.business.infrastructure;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

public class FeignConfiguration {

  /**
   * 创建Feign请求拦截器，在发送请求前设置认证的token,各个微服务将token设置到环境变量中来达到通用
   */
  @Bean
  public Feign1BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new Feign1BasicAuthRequestInterceptor();
  }

  @Autowired
  private ObjectFactory<HttpMessageConverters> messageConverters;

  @Bean
  @Scope("prototype")
  public Encoder feignFormEncoder() {
    Encoder encoder = new FormEncoder(new SpringEncoder(this.messageConverters));
    return encoder;
  }

  public class Feign1BasicAuthRequestInterceptor implements RequestInterceptor {

    public Feign1BasicAuthRequestInterceptor() {

    }

    @Override
    public void apply(RequestTemplate template) {
      template.header("Authorization", "bearer " + System.getProperty("service_call.token"));
    }
  }

}
