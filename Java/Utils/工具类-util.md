# Util

## HTTPUtils

- 获取URLConnection连接
- 获取请求结果
- 获取传输流

```java

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPUtils {
    /**
     * get请求，返回结果转string
     *
     * @param url   发送请求的URL
     * @param param 请求参数
     * @return URL 所代表远程资源的响应结果
     */
    public static String getStringFromHTTPGet(String url, Map<String, Object> param) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream inputStream = null;
        try {
            URL realUrl = new URL(appendParamsToUrl(url, param));
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            inputStream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            assert inputStream != null;
            inputStream.close();
        }
        return result.toString();
    }

    /**
     * 获取 URLConnection
     *
     * @param url url
     * @return URLConnection
     * @throws IOException e
     */
    private static URLConnection setURLConnection(String url) throws IOException {
        // 打开和URL之间的连接
        URLConnection conn = new URL(url).openConnection();
        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        return conn;
    }

    /**
     * 从post请求获取输入流
     *
     * @param url    url
     * @param params params
     * @return InputStream
     * @throws IOException e
     */
    public static InputStream getStreamFromHTTPPost(String url, Map<String, Object> params) throws IOException {
        // 将参数转换为json字符串
        String paramsJson = "{}";
        if (params != null) {
            // 将参数转换为json字符串
            paramsJson = JSONObject.toJSONString(params);
        }
        // 打开和URL之间的连接
        URLConnection conn = setURLConnection(url);
        // 获取URLConnection对象对应的输出流
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        // 发送请求参数
        out.print(paramsJson);
        // flush输出流的缓冲
        out.flush();
        out.close();
        // 定义BufferedReader输入流来读取URL的响应
        return conn.getInputStream();
    }

    /**
     * 通过http发送文件流
     *
     * @param url   url
     * @param file  file
     * @param param param
     * @return String
     * @throws IOException e
     */
    public static String sendStreamThroughHTTPPost(String url, File file, Map<String, Object> param) throws IOException {
        String responseText = "";
        DataInputStream in = null;
        OutputStream out = null;
        InputStream ins = null;
        ByteArrayOutputStream outStream = null;
        HttpURLConnection conn = null;

        try {
            // 打开和URL之间的连接
            conn = (HttpURLConnection) new URL(appendParamsToUrl(url, param)).openConnection();
            // 设置块大小防止OOM
            conn.setChunkedStreamingMode(1024);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            // Post 请求不能使用缓存
            conn.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Charsert", "UTF-8");
            //开启长连接可以持续传输
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setConnectTimeout(99999999);
            conn.connect();

            out = conn.getOutputStream();
            in = new DataInputStream(new FileInputStream(file));

            int bytes = 0;
            byte[] buffer = new byte[1024];
            while ((bytes = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytes);
            }
            out.flush();

            // 返回流
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("返回信息::" + conn.getResponseCode());
                ins = conn.getInputStream();
                outStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int count = -1;
                while ((count = ins.read(data, 0, 1024)) != -1) {
                    outStream.write(data, 0, count);
                }
                responseText = JSONObject.parseObject(new String(outStream.toByteArray(), StandardCharsets.UTF_8)).toString();
            } else {
                throw new Exception("接口错误，代码" + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (ins != null) {
                ins.close();
            }
            if (outStream != null) {
                outStream.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
            // 回收资源
            System.gc();
        }
        return responseText;
    }

    /**
     * 把参数塞进url里
     *
     * @param url   url
     * @param param param
     * @return String
     */
    private static String appendParamsToUrl(String url, Map<String, Object> param) {
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            params.append(entry.getKey());
            params.append("=");
            if (entry.getValue() != null) {
                params.append(entry.getValue().toString());
            }
            params.append("&");
        }
        return url + "?" + params.toString();
    }

    /**
     * 从post请求获取字符返回值
     *
     * @param httpUrl httpUrl
     * @param params  params
     * @return String
     * @throws IOException e
     */
    public static String getStringFromHTTPPost(String httpUrl, Map<String, Object> params) throws IOException {
        InputStream in = getStreamFromHTTPPost(httpUrl, params);
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(in)
        );
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    /**
     * ping ip
     *
     * @param ip ip
     * @return boolean
     */
    public static boolean pingIp(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * ping ip加端口
     *
     * @param ip   ip
     * @param port port
     * @return boolean
     */
    public static boolean pingIpAndPort(String ip, String port) throws IOException {
        if (null == ip || 0 == ip.length() || null == port || 0 == port.length() || isInt(port) || !isRangeInt(port, 1024, 65535)) {
            return false;
        }
        return pingIpAndPort(ip, Integer.parseInt(port));
    }

    /**
     * ping ip加端口
     *
     * @param ip   ip
     * @param port port
     * @return boolean
     */
    public static boolean pingIpAndPort(String ip, int port) throws IOException {
        if (null == ip || 0 == ip.length() || port < 1024 || port > 65535) {
            return false;
        }
        if (!pingIp(ip)) {
            return false;
        }
        Socket s = new Socket();
        try {
            SocketAddress add = new InetSocketAddress(ip, port);
            // 超时3秒
            s.connect(add, 3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            s.close();
        }
    }

    /**
     * 判断是否是整数
     *
     * @param str str
     * @return boolean
     */
    public static boolean isInt(String str) {
        if (!isNumeric(str)) {
            return true;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("[0-9]+");
        // matcher是全匹配
        Matcher isNum = pattern.matcher(str);
        return !isNum.matches();
    }

    /**
     * 匹配是否包含数字
     *
     * @param str str
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        if (null == str || 0 == str.length()) {
            return false;
        }
        if (str.endsWith(".")) {
            return false;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        // matcher是全匹配
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 是否在范围内
     *
     * @param str   str
     * @param start start
     * @param end   end
     * @return boolean
     */
    public static boolean isRangeInt(String str, int start, int end) {
        if (isInt(str)) {
            return false;
        }
        int i = Integer.parseInt(str);
        return i > start && i < end;
    }
}
```

