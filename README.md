**English** | [简体中文](README_CN.md)

# ![image](https://raw.githubusercontent.com/Airsaid/AndroidLocalizePlugin/85cf5020832523ea333ad09286af55880460457a/src/main/resources/META-INF/pluginIcon.svg) AndroidLocalizePlugin
[![Plugin Version](https://img.shields.io/jetbrains/plugin/v/11174)](https://plugins.jetbrains.com/plugin/11174-androidlocalize)
[![Plugin Rating](https://img.shields.io/jetbrains/plugin/r/rating/11174)](https://plugins.jetbrains.com/plugin/11174-androidlocalize)
[![Build](https://github.com/Airsaid/AndroidLocalizePlugin/workflows/Build/badge.svg)](https://github.com/Airsaid/AndroidLocalizePlugin/actions/workflows/build.yml)

<!-- Plugin description -->
[Website](https://plugins.jetbrains.com/plugin/11174-androidlocalize) | [GitHub](https://github.com/Airsaid/AndroidLocalizePlugin) | [Issues](https://github.com/Airsaid/AndroidLocalizePlugin/issues) | [Reviews](https://plugins.jetbrains.com/plugin/11174-androidlocalize/reviews)

Android localization plugin. supports multiple languages and multiple translators.

# Features
- Multiple translator support:
  - Google translator. 
  - Microsoft translator.
  - Baidu translator.
  - Youdao translator.
  - Ali translator.
  - DeepL translator.
  - OpenAI ChatGPT translator.
- Supports up to 100+ languages.
- One key generates all translation files.
- Support no translation of existing string.
- Support for specifying that text is not translated.
- Support for caching translated strings. 
- Support to set the translation interval time.

# Usage
- Step 1: Select the `values/strings.xml`(or any string resource in `values` directory).
- Step 2: Right click and select "Translate to Other Languages".
- Step 3: Select the languages to be translated.
- Step 4: Click OK.

<!-- Plugin description end -->

# Preview
![image](preview/preview.gif)
![image](preview/settings.png)

# Install
[![Install Plugin](preview/install.png)](https://plugins.jetbrains.com/plugin/11174-androidlocalize)

# FAQ
- Q: How to ignore translation?

    A: Use the [translatable or xliff:g](https://developer.android.com/guide/topics/resources/localization#managing-strings) tags. for example:
    ```
    <string name="app_name" translatable="false">HelloAndroid</string>
    <string name="star_rating">Check out our 5<xliff:g id="star">\u2605</xliff:g></string>
    <string name="app_home_url">Visit us at <xliff:g id="application_homepage">https://github.com/Airsaid/AndroidLocalizePlugin</xliff:g></string>
    <string name="prod_name">Learn more at <xliff:g id="game_group">Muggle Game Studio</xliff:g></string>
    ```
    **Note: Display one line without extra line breaks and spaces in between.**
- Q: Translation failure: java.net.HttpRetryException: cannot retry due to redirection, in streaming mode
  
  A: If you are using the default translation engine (Google), then you can try switching to another engine on the settings page and use your own account for translation. Because the default translation engine is not stable.

# ChangeLog
[ChangeLog](CHANGELOG.md)

# Support and Donations

You can contribute and support this project by doing any of the following:

- Star the project on GitHub.
- Give feedback.
- Commit PR.
- Contribute your ideas/suggestions.
- Share the plugin with your friends/colleagues.
- If you like the plugin, please consider making a donation to keep the plugin active:

  <table>
    <thead align="center">
      <tr>
        <th><a href="https://opencollective.com/androidlocalizeplugin" target="_blank">Open Collective</a></th>
        <th><a href="https://pay.weixin.qq.com/index.php/public/wechatpay_en" target="_blank">WeChat Pay</a></th>
        <th><a href="https://global.alipay.com" target="_blank">Alipay</a></th>
      </tr>
    </thead>
    <tr align="center">
      <td>
        <a href="https://opencollective.com/androidlocalizeplugin/donate" target="_blank">
          <img src="https://raw.githubusercontent.com/Airsaid/Resources/master/Images/opencollective-logo.png" width=298 alt="Donate To Our Collective">
        </a>
      </td>
      <td>
        <a href="https://pay.weixin.qq.com/index.php/public/wechatpay_en" target="_blank">
          <img src="https://raw.githubusercontent.com/Airsaid/Resources/master/Images/AndroidLocalizePlugin_WeChatPay.jpg" alt="WeChat Play">
        </a>
      </td>
      <td>
        <a href="https://global.alipay.com" target="_blank">
          <img src="https://raw.githubusercontent.com/Airsaid/Resources/master/Images/AndroidLocalizePlugin_Alipay.jpg" alt="Alipay">
        </a>
      </td>
    </tr>
  </table>

**Thank you for your support!**

# Sponsors
[![Development powered by JetBrains](https://pic.stackoverflow.wiki/uploadImages/111/201/226/60/2021/06/20/18/45/3aba65f5-1231-4c9a-817f-83cd5a29fd0c.svg)](https://jb.gg/OpenSourc)

# License
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
