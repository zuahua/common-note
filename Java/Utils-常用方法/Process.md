### Process

[java Process类详解！ - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/44957705)

```java
public CommonResult runModelByVersion(ForecastTempDTO forecastTempDTO) {
    CommonResult commonResult = new CommonResult();

    String script = "python ";

    log.info("=====> 准备执行python脚本预测");
    // 执行
    Process process = null;
    try {
        process = Runtime.getRuntime().exec(script);
        if (process.waitFor() != 0) {
            commonResult.setSuccess(false, 2500, "预测失败");
        } else {
            commonResult.setSuccess(true, 200, "预测成功");
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        commonResult.setSuccess(false, 2500, "预测失败");
    } finally {
        if (process != null) {
            process.destroy();
        }
    }

    log.info("=====> 结束");

    return commonResult;
}
```

