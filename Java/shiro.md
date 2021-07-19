## shiro

教程：

<https://blog.csdn.net/u010606397/article/details/104110093>

## 问题解决

### shiro doGetAuthenticationInfo 后端拦截异常，并向前端返回json结果

<[shiro doGetAuthenticationInfo 后端拦截异常，并向前端返回json结果_聪聪那年22的博客-CSDN博客](https://blog.csdn.net/qq_40898500/article/details/111926556)>

> class JwtAccessControlFilter extends AccessControlFilter

```java
/**
     * 认证失败处理
     */
private void responseHandle(ServletResponse response) {
    HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    httpServletResponse.setCharacterEncoding("UTF-8");
    httpServletResponse.setContentType("application/json; charset=utf-8");
    try (PrintWriter out = httpServletResponse.getWriter()) {
        CommonResult result = new CommonResult();
        result.setSuccess(false, 2332, "认证失败");
        out.append(JSON.toJSONString(result));
    } catch (IOException e) {
        throw new AuthenticationException("token认证失败！");
    }
}

// ...

@Override
protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    // 验证是否包含token
    String tokenStr = httpServletRequest.getHeader("token");

    if (tokenStr == null || "".equals(tokenStr)) {
        // 认证失败
        responseHandle(response);
        return false;
    }

    // 验证token有效性
    Subject subject = SecurityUtils.getSubject();
    JwtAuthenticationToken token = new JwtAuthenticationToken(tokenStr);
    try {
        subject.login(token);
        return true;
    } catch (Exception ignored) {
    }

    // 认证失败
    responseHandle(response);
    return false;
}
```

