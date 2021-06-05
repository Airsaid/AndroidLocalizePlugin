# AndroidLocalizePlugin
[![Plugin Version](https://img.shields.io/jetbrains/plugin/v/11174)](https://plugins.jetbrains.com/plugin/11174-androidlocalize)
[![Plugin Rating](https://img.shields.io/jetbrains/plugin/r/rating/11174)](https://plugins.jetbrains.com/plugin/11174-androidlocalize)

:earth_asia: Android/IDEA localization plugin. supports multiple languages and multiple translators. ([中文文档](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/README_CN.md))

# Features
- Multiple translator support:
  - Google translator. 
  - Microsoft translator.
  - Baidu translator.
  - Youdao translator.
- Supports up to 100+ languages.
- One key generates all translation files.
- Support no translation of existing string.
- Support for specifying that text is not translated.
- Support for caching translated strings.

# Preview
![image](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/preview.gif)
![image](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/settings.png)

# Install
[![Install Plugin](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/install.png)](https://plugins.jetbrains.com/plugin/11174-androidlocalize)

# Usage
- Step 1: Select the values/strings.xml.
- Step 2: Right click and select "Translate to Other Languages".
- Step 3: Select the languages to be translated.
- Step 4: Click OK.

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

# ChangeLog
[Change Log List](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/CHANGELOG.md)

# ContactMe
- Telegram: [https://t.me/airsaids/](https://t.me/airsaids/)

# Invite me for coffee :coffee:
If this project has helped you, you can [invite me for coffee](https://25e37ece.wiz03.com/wapp/pages/view/share/s/0BUTXe15Q4mk28KWtW0l7BLh1Y6ijp02l4Ct2gxqhW0OmYvl).

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