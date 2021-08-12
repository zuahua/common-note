### SpringBoot 过滤器的使用

<https://www.jdon.com/springboot/spring-filter.html>

### 开发过程中的问题

#### 2.1 springboot 本地没问题 打包后出现 required request body is missing

**问题复现：**

> 为了解决springboot请求体中的流只能读取一次的问题，需要自定义过滤器替换原来的 `HttpServletRequest`;
>
> 过滤器使用方法：主程序添加`@ServletComponentScan`注解；过滤器使用`@WebFilter`注解

**问题原因：**

> 使用以上过滤器方式，**只在使用嵌入服务器才有效**，导致本地运行没问题，打包发布在**Tomcat** **webapp**下出现 **required request body is missing**错误

**问题解决：**

使用 `FilterRegistrationBean`注册过滤器的方式：

`WebConfig.java` 配置类

```java
@Configuration
public class WebConfig {
    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean<BodyReaderFilter> bodyReaderFilterFilterRegistrationBean() {
        FilterRegistrationBean<BodyReaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BodyReaderFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
```

`BodyReaderFilter.java` 过滤器类，用自定义的`HttpServletRequest`替换原来的

```java
public class BodyReaderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
        }
        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
```

自定义的`HttpServletRequest` :`BodyReaderHttpServletRequestWrapper.java`

```java
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 用于将流保存下来
     */
    private byte[] requestBody = null;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {

        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);

        return new ServletInputStream() {

            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
```

### 过滤器跨域

- 实现 的 Filter

```java
package com.imagesky.gis.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyCorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
```

- 再去`WebConfig.java` 配置类注册过滤器

```java
package com.imagesky.gis.config;

import com.imagesky.gis.filter.BodyReaderFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhua
 * @date 2021/7/24 19:14
 * @description 配置 bean 注册
 */
@Configuration
public class WebConfig {
    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean<BodyReaderFilter> bodyReaderFilterFilterRegistrationBean() {
        FilterRegistrationBean<BodyReaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BodyReaderFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    /**
     * 注册CORS过滤器
     */
    @Bean
    public FilterRegistrationBean<MyCorsFilter> corsFilterFilterRegistrationBean() {
        FilterRegistrationBean<MyCorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MyCorsFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
```

