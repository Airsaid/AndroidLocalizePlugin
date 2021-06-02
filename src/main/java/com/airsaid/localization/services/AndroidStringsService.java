package com.airsaid.localization.services;

import com.airsaid.localization.model.AndroidString;
import com.airsaid.localization.translate.lang.Lang;
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
 * Operation service for the {@link #NAME_STRINGS_FILE} file.
 *
 * @author airsaid
 */
@Service
public final class AndroidStringsService {

  private static final Logger LOG = Logger.getInstance(AndroidStringsService.class);

  private static final String NAME_STRINGS_FILE = "strings.xml";

  public static AndroidStringsService getInstance() {
    return ServiceManager.getService(AndroidStringsService.class);
  }

  /**
   * Asynchronous loading the {@link #NAME_STRINGS_FILE} file as the {@link AndroidString} collection.
   *
   * @param stringsFile {@link #NAME_STRINGS_FILE} file.
   * @param consumer    load result. called in the event dispatch thread.
   */
  public void loadStringsByAsync(@NotNull PsiFile stringsFile, @NotNull Consumer<List<AndroidString>> consumer) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
          List<AndroidString> androidStrings = loadStrings(stringsFile);
          ApplicationManager.getApplication().invokeLater(() ->
              consumer.consume(androidStrings));
        }
    );
  }

  /**
   * Loading the {@link #NAME_STRINGS_FILE} file as the {@link AndroidString} collection.
   *
   * @param stringsFile {@link #NAME_STRINGS_FILE} file.
   * @return {@link AndroidString} collection.
   */
  public List<AndroidString> loadStrings(@NotNull PsiFile stringsFile) {
    return ApplicationManager.getApplication().runReadAction((Computable<List<AndroidString>>) () -> {
      LOG.info("LoadStrings stringsFile: " + stringsFile.getName());
      List<AndroidString> androidStrings = parseStringsXml(stringsFile);
      LOG.info("Parsed " + stringsFile.getName() + " result: " + androidStrings);
      return androidStrings;
    });
  }

  /**
   * Write {@link AndroidString} collection data to the specified file.
   *
   * @param strings     specified {@link AndroidString} collection data.
   * @param stringsFile specified file.
   */
  public void writeStringsFile(List<AndroidString> strings, @NotNull File stringsFile) {
    boolean isCreateSuccess = FileUtil.createIfDoesntExist(stringsFile);
    if (!isCreateSuccess) {
      LOG.error("Failed to write to " + stringsFile.getPath() + " file: create failed!");
      return;
    }
    ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
      try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stringsFile, false), StandardCharsets.UTF_8))) {
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
        LOG.error("Failed to write to " + stringsFile.getPath() + " file.", e);
      }
    }));
  }

  /**
   * Verify that the file is a values/{@link #NAME_STRINGS_FILE} file.
   *
   * @param file verify file.
   * @return true: the file is values/{@link #NAME_STRINGS_FILE} file.
   */
  public boolean isStringsFile(@Nullable PsiFile file) {
    if (file == null) return false;

    PsiDirectory parent = file.getParent();
    if (parent == null) return false;

    String parentName = parent.getName();
    if (!"values".equals(parentName)) return false;

    return NAME_STRINGS_FILE.equals(file.getName());
  }

  /**
   * Parse the {@link #NAME_STRINGS_FILE} corresponding to {@link PsiFile}.
   *
   * @param stringsFile {@link #NAME_STRINGS_FILE} psi file.
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
      final XmlTagChild[] contentTags = stringTag.getValue().getChildren();
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
      androidStrings.add(new AndroidString(name, contents, translatable));
    }
    return androidStrings;
  }

  /**
   * Get the {@link #NAME_STRINGS_FILE} file of the specified language in the specified project resource directory.
   *
   * @param project     current project.
   * @param resourceDir specified resource directory.
   * @param lang        specified language.
   * @return null if not exist, otherwise return the {@link #NAME_STRINGS_FILE} file.
   */
  @Nullable
  public PsiFile getStringsPsiFile(@NotNull Project project, @NotNull VirtualFile resourceDir, @NotNull Lang lang) {
    Objects.requireNonNull(project);
    Objects.requireNonNull(resourceDir);
    Objects.requireNonNull(lang);

    return ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
      VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(getStringsFile(resourceDir, lang));
      if (virtualFile == null) {
        return null;
      }
      return PsiManager.getInstance(project).findFile(virtualFile);
    });
  }

  /**
   * Get the {@link #NAME_STRINGS_FILE} file in the values directory of the specified language in the resource directory.
   *
   * @param resourceDir specified resource directory.
   * @param lang        specified language.
   * @return {@link #NAME_STRINGS_FILE} file.
   */
  @NotNull
  public File getStringsFile(@NotNull VirtualFile resourceDir, @NotNull Lang lang) {
    return new File(resourceDir.getPath().concat(File.separator).concat(getValuesDirectoryName(lang)), NAME_STRINGS_FILE);
  }

  private String getValuesDirectoryName(@NotNull Lang lang) {
    String suffix;
    String langCode = lang.getCode();
    if (langCode.equals(Lang.CHINESE_SIMPLIFIED.getCode())) {
      suffix = "zh-rCN";
    } else if (langCode.equals(Lang.CHINESE_TRADITIONAL.getCode())) {
      suffix = "zh-rTW";
    } else if (langCode.equals(Lang.FILIPINO.getCode())) {
      suffix = "fil";
    } else if (langCode.equals(Lang.INDONESIAN.getCode())) {
      suffix = "in-rID";
    } else if (langCode.equals(Lang.JAVANESE.getCode())) {
      suffix = "jv";
    } else {
      suffix = langCode;
    }
    return "values-".concat(suffix);
  }

}
