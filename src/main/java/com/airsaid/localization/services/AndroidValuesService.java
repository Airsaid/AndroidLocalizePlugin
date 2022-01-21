/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.services;

import com.airsaid.localization.model.*;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.*;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Operation service for the android value files. eg: strings.xml or plurals.xml or arrays.xml.
 *
 * @author airsaid
 */
@Service
public final class AndroidValuesService {

  private static final Logger LOG = Logger.getInstance(AndroidValuesService.class);

  private static final String NAME_STRINGS_FILE = "strings.xml";
  private static final String NAME_PLURALS_FILE = "plurals.xml";
  private static final String NAME_ARRAYS_FILE = "arrays.xml";

  private static final String NAME_TAG_STRING = "string";
  private static final String NAME_TAG_PLURALS = "plurals";
  private static final String NAME_TAG_STRING_ARRAY = "string-array";

  /**
   * Returns the {@link AndroidValuesService} object instance.
   *
   * @return the {@link AndroidValuesService} object instance.
   */
  public static AndroidValuesService getInstance() {
    return ServiceManager.getService(AndroidValuesService.class);
  }

  /**
   * Asynchronous loading the value file as the {@link AbstractValue} collection.
   *
   * @param valueFile the value file.
   * @param consumer  load result. called in the event dispatch thread.
   */
  public void loadValuesByAsync(@NotNull PsiFile valueFile, @NotNull Consumer<List<AbstractValue>> consumer) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
          List<AbstractValue> values = loadValues(valueFile);
          ApplicationManager.getApplication().invokeLater(() ->
              consumer.consume(values));
        }
    );
  }

  /**
   * Loading the value file as the {@link AbstractValue} collection.
   *
   * @param valueFile the value file.
   * @return {@link AbstractValue} collection.
   */
  public List<AbstractValue> loadValues(@NotNull PsiFile valueFile) {
    return ApplicationManager.getApplication().runReadAction((Computable<List<AbstractValue>>) () -> {
      LOG.info("LoadValues valueFile: " + valueFile.getName());
      List<AbstractValue> values = parseValuesXml(valueFile);
      LOG.info("Parsed " + valueFile.getName() + " result: " + values);
      return values;
    });
  }

  private List<AbstractValue> parseValuesXml(@NotNull PsiFile valueFile) {
    final List<AbstractValue> values = new ArrayList<>();
    final XmlFile xmlFile = (XmlFile) valueFile;

    final XmlDocument document = xmlFile.getDocument();
    if (document == null) return values;

    final XmlTag rootTag = document.getRootTag();
    if (rootTag == null) return values;

    XmlTag[] subTags = rootTag.getSubTags();
    for (XmlTag subTag : subTags) {
      String subTagName = subTag.getName();
      String name = subTag.getAttributeValue("name");
      String translatableStr = subTag.getAttributeValue("translatable");
      boolean translatable = Boolean.parseBoolean(translatableStr == null ? "true" : translatableStr);

      switch (subTagName) {
        case NAME_TAG_STRING:
          values.add(new StringValue(name, translatable, parseContent(subTag)));
          break;
        case NAME_TAG_PLURALS:
          values.add(new PluralsValue(name, translatable, parsePluralsItems(subTag)));
          break;
        case NAME_TAG_STRING_ARRAY:
          values.add(new StringArrayValue(name, translatable, parseStringArrayItems(subTag)));
          break;
      }
    }
    return values;
  }

  private List<Content> parseContent(XmlTag contentTag) {
    List<Content> contents = new ArrayList<>();
    XmlTagChild[] contentTags = contentTag.getValue().getChildren();
    for (XmlTagChild content : contentTags) {
      if (content instanceof XmlText) {
        final XmlText xmlText = (XmlText) content;
        final String text = xmlText.getValue();
        contents.add(new Content(text));
      } else if (content instanceof XmlTag) {
        final XmlTag xmlTag = (XmlTag) content;
        if (!xmlTag.getName().equals("xliff:g")) continue;

        final String text = xmlTag.getValue().getText();
        final String id = xmlTag.getAttributeValue("id");
        final String example = xmlTag.getAttributeValue("example");
        contents.add(new Content(text, id, example, true));
      }
    }
    return contents;
  }

  private List<PluralsValue.Item> parsePluralsItems(XmlTag pluralsTag) {
    List<PluralsValue.Item> items = new ArrayList<>();
    XmlTag[] itemTags = pluralsTag.getSubTags();
    for (XmlTag itemTag : itemTags) {
      String quantity = itemTag.getAttributeValue("quantity");
      List<Content> contents = parseContent(itemTag);
      items.add(new PluralsValue.Item(quantity, contents));
    }
    return items;
  }

  private List<StringArrayValue.Item> parseStringArrayItems(XmlTag stringArrayTag) {
    List<StringArrayValue.Item> items = new ArrayList<>();
    XmlTag[] itemTags = stringArrayTag.getSubTags();
    for (XmlTag itemTag : itemTags) {
      List<Content> contents = parseContent(itemTag);
      items.add(new StringArrayValue.Item(contents));
    }
    return items;
  }

  /**
   * Write {@link AbstractValue} collection data to the specified file.
   *
   * @param values    specified {@link AbstractValue} collection data.
   * @param valueFile specified file.
   */
  public void writeValueFile(List<AbstractValue> values, @NotNull File valueFile) {
    boolean isCreateSuccess = FileUtil.createIfDoesntExist(valueFile);
    if (!isCreateSuccess) {
      LOG.error("Failed to write to " + valueFile.getPath() + " file: create failed!");
      return;
    }
    ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
      try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueFile, false), StandardCharsets.UTF_8))) {
        bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        bw.newLine();
        bw.write("<resources>");
        bw.newLine();
        for (AbstractValue value : values) {
          if (value instanceof StringValue) {
            writeStringValue(bw, (StringValue) value);
          } else if (value instanceof PluralsValue) {
            writePluralsValue(bw, (PluralsValue) value);
          } else if (value instanceof StringArrayValue) {
            writeStringArrayValue(bw, (StringArrayValue) value);
          }
          bw.newLine();
        }
        bw.write("</resources>");
        bw.flush();
      } catch (IOException e) {
        e.printStackTrace();
        LOG.error("Failed to write to " + valueFile.getPath() + " file.", e);
      }
    }));
  }

  private void writeStringValue(BufferedWriter bw, StringValue value) throws IOException {
    bw.write("\t<" + NAME_TAG_STRING + " name=\"" + value.getName() + "\">");
    for (Content content : value.getContents()) {
      bw.write(content.getText());
    }
    bw.write("</" + NAME_TAG_STRING + ">");
  }

  private void writePluralsValue(BufferedWriter bw, PluralsValue value) throws IOException {
    bw.write("\t<" + NAME_TAG_PLURALS + " name=\"" + value.getName() + "\">");
    bw.newLine();
    for (PluralsValue.Item item : value.getItems()) {
      bw.write("\t\t<item quantity=\"" + item.getQuantityName() + "\">");
      for (Content content : item.getContents()) {
        bw.write(content.getText());
      }
      bw.write("</item>");
      bw.newLine();
    }
    bw.write("\t</" + NAME_TAG_PLURALS + ">");
  }

  private void writeStringArrayValue(BufferedWriter bw, StringArrayValue value) throws IOException {
    bw.write("\t<" + NAME_TAG_STRING_ARRAY + " name=\"" + value.getName() + "\">");
    bw.newLine();
    for (StringArrayValue.Item item : value.getItems()) {
      bw.write("\t\t<item>");
      for (Content content : item.getContents()) {
        bw.write(content.getText());
      }
      bw.write("</item>");
      bw.newLine();
    }
    bw.write("\t</" + NAME_TAG_STRING_ARRAY + ">");
  }

  /**
   * Verify that the file is a values/string.xml or plurals.xml or arrays.xml file.
   *
   * @param file the verify file.
   * @return true: the file is values/string.xml or plurals.xml or arrays.xml file.
   */
  public boolean isValueFile(@Nullable PsiFile file) {
    if (file == null) return false;

    PsiDirectory parent = file.getParent();
    if (parent == null) return false;

    String parentName = parent.getName();
    if (!"values".equals(parentName)) return false;

    String fileName = file.getName();
    return NAME_STRINGS_FILE.equals(fileName) ||
        NAME_PLURALS_FILE.equals(fileName) ||
        NAME_ARRAYS_FILE.equals(fileName);
  }

  /**
   * Get the value file of the specified language in the specified project resource directory.
   *
   * @param project     current project.
   * @param resourceDir specified resource directory.
   * @param lang        specified language.
   * @param fileName    the name of value file.
   * @return null if not exist, otherwise return the value file.
   */
  @Nullable
  public PsiFile getValuePsiFile(@NotNull Project project, @NotNull VirtualFile resourceDir,
                                 @NotNull Lang lang, @NotNull String fileName) {
    Objects.requireNonNull(project);
    Objects.requireNonNull(resourceDir);
    Objects.requireNonNull(lang);
    Objects.requireNonNull(fileName);

    return ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
      VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(getValueFile(resourceDir, lang, fileName));
      if (virtualFile == null) {
        return null;
      }
      return PsiManager.getInstance(project).findFile(virtualFile);
    });
  }

  /**
   * Get the value file in the {@code values} directory of the specified language in the resource directory.
   *
   * @param resourceDir specified resource directory.
   * @param lang        specified language.
   * @param fileName    the name of value file.
   * @return the value file.
   */
  @NotNull
  public File getValueFile(@NotNull VirtualFile resourceDir, @NotNull Lang lang, String fileName) {
    return new File(resourceDir.getPath().concat(File.separator).concat(getValuesDirectoryName(lang)), fileName);
  }

  private String getValuesDirectoryName(@NotNull Lang lang) {
    String suffix;
    if (lang.equals(Languages.CHINESE_SIMPLIFIED)) {
      suffix = "zh-rCN";
    } else if (lang.equals(Languages.CHINESE_TRADITIONAL)) {
      suffix = "zh-rTW";
    } else if (lang.equals(Languages.FILIPINO)) {
      suffix = "fil";
    } else if (lang.equals(Languages.INDONESIAN)) {
      suffix = "in-rID";
    } else if (lang.equals(Languages.JAVANESE)) {
      suffix = "jv";
    } else {
      suffix = lang.getCode();
    }
    return "values-".concat(suffix);
  }

}
