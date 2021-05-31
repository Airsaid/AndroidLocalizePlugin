package com.airsaid.localization.utils;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * @author airsaid
 */
public class NotificationUtil {

  private static final String NOTIFICATION_GROUP_ID = "com.github.airsaid.androidlocalize.Notification";

  private static final NotificationGroup NOTIFICATION_GROUP =
      new NotificationGroup(NOTIFICATION_GROUP_ID, NotificationDisplayType.BALLOON, true);

  private NotificationUtil() {
    throw new AssertionError("No com.airsaid.localization.utils.NotificationUtil instances for you!");
  }

  public static void notifyInfo(@Nullable Project project, String content) {
    NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION)
        .notify(project);
  }

  public static void notifyWarning(@Nullable Project project, String content) {
    NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING)
        .notify(project);
  }

  public static void notifyError(@Nullable Project project, String content) {
    NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR)
        .notify(project);
  }

}
