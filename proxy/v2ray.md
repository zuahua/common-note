## v2ray

### 路由规则配置

- 使用 <https://github.com/Loyalsoldier/v2ray-rules-dat> 路由文件增强版

- 下载　geoip.dat 、geosite.dat ，替换v2rayN 安装文件下的

- 配置方法

  - 路由设置 -> 高级功能
  - 点击进入规则集设置
  - 剪切板导入吃以下规则

  ```json
  [
    {
      "outboundTag": "block",
      "domain": [
        "geosite:category-ads-all",
        "geosite:win-spy"
      ]
    },
    {
      "outboundTag": "direct",
      "domain": [
        "geosite:private",
        "geosite:apple-cn",
        "geosite:google-cn",
        "geosite:tld-cn",
        "geosite:cn",
        "geosite:category-games"
      ]
    },
    {
      "port": "0-65535",
      "outboundTag": "proxy",
      "ip": [
        "geoip:telegram"
      ],
      "domain": [
        "geosite:tld-!cn",
        "geosite:gfw",
        "geosite:greatfire",
        "geosite:geolocation-!cn"
      ]
    }
  ]
  ```

  - 白名单模式
  
  ```json
  [
    {
      "port": "",
      "outboundTag": "proxy",
      "ip": [],
      "domain": [
        "#以下三行是GitHub网站，为了不影响下载速度走代理",
        "github.com",
        "githubassets.com",
        "githubusercontent.com"
      ],
      "protocol": []
    },
    {
      "type": "field",
      "outboundTag": "block",
      "domain": [
        "#阻止CrxMouse鼠标手势收集上网数据",
        "mousegesturesapi.com",
        "#下一行广告管理平台网址，在ProductivityTab（原iChrome）浏览器插件页面显示",
        "cf-se.com"
      ]
    },
    {
      "type": "field",
      "port": "",
      "outboundTag": "direct",
      "ip": [
        "geoip:private",
        "geoip:cn"
      ],
      "domain": [
        "bitwarden.com",
        "bitwarden.net",
        "gravatar.com",
        "gstatic.com",
        "baiyunju.cc",
        "letsencrypt.org",
        "adblockplus.org",
        "safesugar.net",
        "#下两行谷歌广告",
        "googleads.g.doubleclick.net",
        "adservice.google.com",
        "#【以下全部是geo预定义域名列表】",
        "#下一行包含所有私有域名",
        "geosite:private",
        "#下一行包含常见大陆站点域名和CNNIC管理的大陆域名，即geolocation-cn和tld-cn的合集",
        "geosite:cn",
        "#下一行包含所有Adobe旗下域名",
        "geosite:adobe",
        "#下一行包含所有Adobe正版激活域名",
        "geosite:adobe-activation",
        "#下一行包含所有微软旗下域名",
        "geosite:microsoft",
        "#下一行包含微软msn相关域名少数与上一行微软列表重复",
        "geosite:msn",
        "#下一行包含所有苹果旗下域名",
        "geosite:apple",
        "#下一行包含所有广告平台、提供商域名",
        "geosite:category-ads-all",
        "#下一行包含可直连访问谷歌网址，需要替换为加强版GEO文件，如已手动更新为加强版GEO文件，删除此行前面的#号使其生效",
        "geosite:google-cn",
        "#下一行包含可直连访问苹果网址，需要替换为加强版GEO文件，如已手动更新为加强版GEO文件，删除此行前面的#号使其生效",
        "geosite:apple-cn"
      ],
      "protocol": []
    },
    {
      "type": "field",
      "port": "0-65535",
      "outboundTag": "proxy"
    }
  ]
  ```
  
  

### 微软商店、UWP 不走代理解决

管理员 CMD运行：

```shell
# 解除全部uwp应用的网络隔离
FOR /F "tokens=11 delims=\" %p IN ('REG QUERY "HKCU\Software\Classes\Local Settings\Software\Microsoft\Windows\CurrentVersion\AppContainer\Mappings"') DO CheckNetIsolation.exe LoopbackExempt -a -p=%p
```

### v2rayNG 手机上谷歌商店、油管不走代理

需要将`services.googleapis.cn`走代理。
