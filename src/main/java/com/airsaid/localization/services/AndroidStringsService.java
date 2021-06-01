package com.airsaid.localization.services;

import com.airsaid.localization.module.AndroidString;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.*;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
@Service
public final class AndroidStringsService {

  private static final Logger LOG = Logger.getInstance(AndroidStringsService.class);

  public static AndroidStringsService getInstance() {
    return ServiceManager.getService(AndroidStringsService.class);
  }

  public void loadStrings(@NotNull PsiFile stringsFile, @NotNull Consumer<List<AndroidString>> consumer) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> ApplicationManager.getApplication().runReadAction(() ->
        ApplicationManager.getApplication().invokeLater(() -> consumer.consume(loadStrings(stringsFile)))));
  }

  public List<AndroidString> loadStrings(@NotNull PsiFile stringsFile) {
    LOG.info("LoadStrings stringsFile: " + stringsFile);
    List<AndroidString> androidStrings = parseStringsXml(stringsFile);
    LOG.info("Parsed strings.xml result: " + androidStrings);
    return androidStrings;
  }

  public void writeStringsFile(File file, List<AndroidString> strings) {
    ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
      try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
        bw.write("<resources>");
        bw.newLine();
        for (AndroidString androidString : strings) {
          bw.write("\t<string name=\"" + androidString.getName() + "\">");
          for (AndroidString.Content content : androidString.getContents()) {
            bw.write(content.getText());
          }
          bw.write("</string>");
          bw.newLine();
        }
        bw.write("</resources>");
        bw.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));
  }

  /**
   * Verify that the file is a strings.xml file.
   *
   * @param file verify file.
   * @return true: the file is strings.xml file.
   */
  public boolean isStringsFile(@Nullable PsiFile file) {
    if (file == null) return false;

    PsiDirectory parent = file.getParent();
    if (parent == null) return false;

    String parentName = parent.getName();
    if (!"values".equals(parentName)) return false;

    return "strings.xml".equals(file.getName());
  }

  /**
   * Parse the strings.xml corresponding to {@link PsiFile}.
   *
   * @param stringsFile strings.xml psi file.
   * @return parsed android string list.
   */
  private List<AndroidString> parseStringsXml(@NotNull PsiFile stringsFile) {
    final List<AndroidString> androidStrings = new ArrayList<>();
    final XmlFile xmlFile = (XmlFile) stringsFile;
    final XmlDocument document = xmlFile.getDocument();
    if (document == null) return androidStrings;

    final XmlTag rootTag = document.getRootTag();
    if (rootTag == null) return androidStrings;

    final XmlTag[] stringTags = rootTag.findSubTags("string");
    for (XmlTag stringTag : stringTags) {
      final String name = stringTag.getAttributeValue("name");
      final String translatableStr = stringTag.getAttributeValue("translatable");
      final boolean translatable = Boolean.parseBoolean(translatableStr == null ? "true" : translatableStr);

      final List<AndroidString.Content> contents = new ArrayList<>();
      final XmlTagValue value = stringTag.getValue();
      final String valueText = value.getText();
      final XmlTagChild[] contentTags = value.getChildren();
      for (XmlTagChild content : contentTags) {
        if (content instanceof XmlText) {
          final XmlText xmlText = (XmlText) content;
          final String text = xmlText.getValue();
          contents.add(new AndroidString.Content(text));
        } else if (content instanceof XmlTag) {
          final XmlTag xmlTag = (XmlTag) content;
          if (!xmlTag.getName().equals("xliff:g")) continue;

          final String text = xmlTag.getValue().getText();
          final String id = xmlTag.getAttributeValue("id");
          final String example = xmlTag.getAttributeValue("example");
          contents.add(new AndroidString.Content(text, id, example, true));
        }
      }
      androidStrings.add(new AndroidString(name, valueText, contents, translatable));
    }
    return androidStrings;
  }

}
