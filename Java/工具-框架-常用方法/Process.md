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

#### 2. 打印输出信息流、错误流

```java
/**
     * 打印 CMD 执行流
     *
     * @param process pr
     * @throws IOException e
     */
public void printCmdStream(Process process) throws IOException {
    SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
    // //设置编码格式并转换为输入流
    InputStreamReader inst = new InputStreamReader(sis, "GBK");
    // //输入流缓冲区
    BufferedReader br = new BufferedReader(inst);

    String res = null;
    StringBuilder sb = new StringBuilder();
    //循环读取缓冲区中的数据
    while ((res = br.readLine()) != null) {
        sb.append(res).append("\n");
    }
    br.close();
    log.info(sb);
}
```

#### 3. 打印错误流

```java
public void printCmdErrorStream(Process process) throws IOException {
    InputStream errorStream = process.getErrorStream();
    InputStreamReader isr = new InputStreamReader(errorStream);
    BufferedReader br = new BufferedReader(isr);

    String res = null;
    StringBuffer sb = new StringBuffer();
    //循环读取缓冲区中的数据
    while ((res = br.readLine()) != null) {
        sb.append(res).append("\n");
    }
    br.close();
    log.info(sb);
}
```

