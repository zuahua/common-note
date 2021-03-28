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

  