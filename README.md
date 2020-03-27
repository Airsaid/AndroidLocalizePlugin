# AndroidLocalizePlugin
:earth_asia: Android/IDEA localization plugin. support multiple languages, no need to apply for key. ([中文文档](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/README_CN.md))

# Feature
- Support 104 languages.
- No need to apply for key.
- One key generates all translation files.
- Support no translation of existing string.
- Support for specifying that text is not translated.

# Preview
![image](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/preview.gif)

# Install
## Online installation
- Step 1: Open AndroidStudio or IDEA.
- Step 2: Preferences -> Plugins -> Browse repositories...
- Step 3: Search AndroidLocalize and install. Then restart IDE.

## Local installation
- Step 1: Download AndroidLocalizePlugin.zip file.
- Step 2: Open AndroidStudio or IDEA.
- Step 3: Preferences -> Plugins -> Install plugin from disk...
- Step 4: Select AndroidLocalizePlugin.zip and Restart IDE.

# Usage
- Step 1: Select the values/strings.xml.
- Step 2: Right-click and select Convert to other languages.
- Step 3: Select the language to be translated.
- Step 4: Click ok.

# FAQ
- Q1, Empty characters after translation?
    A: You need to set an proxy or replace an proxy address or wait a while before trying. set proxy:
    ![image](https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/preview/setproxy.png)

- Q2, How to ignore translation?
    A: Use the translatable or xliff:g tags. for example:
    ```
    <string name="name" translatable="false">AndroidLocalizePlugin</string>
    <string name="detail">detail<xliff:g>: %1$s</xliff:g></string>
    ```

# Thanks
- MTrans：[https://github.com/hujingshuang/MTrans](https://github.com/hujingshuang/MTrans)

# ContactMe
- Telegram: [https://t.me/airsaids/](https://t.me/airsaids/)

# Invite me for coffee
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
