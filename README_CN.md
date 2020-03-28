# AndroidLocalizePlugin
:earth_asia: Android/IDEA 本地化插件。 支持多种语言，无需申请 KEY。

# 功能
- 支持 104 种语言。
- 无需申请任何 KEY。
- 一键生成所有翻译文件。
- 支持对已存在的文本不翻译。
- 支持对指定的文本不参与翻译。

# 预览
![image](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/preview.gif)

# 安装
## 在线安装
- 第一步：打开 AndroidStudio 或 IDEA。
- 第二步：打开 Preferences -> Plugins -> Browse repositories。
- 第三步：搜索 AndroidLocalize 并安装，然后重新启动 IDE。

## 本地安装
- 第一步：下载 AndroidLocalizePlugin.zip 文件。
- 第二步：打开 AndroidStudio 或 IDEA。
- 第三步：打开 Preferences -> Plugins -> Install plugin from disk。
- 第四步：选择 AndroidLocalizePlugin.zip 文件安装，并重新启动 IDE。

# 使用
- 第一步：选择 values/strings.xml 文件。
- 第二步：右键选择 Convert to other languages。
- 第三步：勾选上需要翻译的语言。
- 第四步：点击 OK。

# 常见问题
- 问题一：翻译后得到的是空的文本？

    回答：你需要设置代理或替换代理地址，或者等待一段时间后再次尝试。代理设置：

    ![image](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/setproxy.png)

- 问题二：如何忽略不让其翻译？

    回答: 可以使用 translatable 或者 xliff:g 标签。示例：
    ```
    <string name="name" translatable="false">AndroidLocalizePlugin</string>
    <string name="detail">detail<xliff:g>: %1$s</xliff:g></string>
    ```

# 感谢
- MTrans：[https://github.com/hujingshuang/MTrans](https://github.com/hujingshuang/MTrans)

# 联系我
- Telegram: [https://t.me/airsaids/](https://t.me/airsaids/)

# 请我喝咖啡
如果这个项目给你带来了帮助，你可以[请我喝咖啡](https://25e37ece.wiz03.com/wapp/pages/view/share/s/0BUTXe15Q4mk28KWtW0l7BLh1Y6ijp02l4Ct2gxqhW0OmYvl)。


# 许可证
```
Copyright 2018 Airsaid. https://github.com/airsaid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
