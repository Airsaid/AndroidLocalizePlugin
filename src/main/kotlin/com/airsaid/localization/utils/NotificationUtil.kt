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

package com.airsaid.localization.utils

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * @author airsaid
 */
object NotificationUtil {

  private const val NOTIFICATION_GROUP_ID = "Android Localize Plugin"

  private val NOTIFICATION_GROUP: NotificationGroup =
    NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID)

  fun notifyInfo(project: Project?, content: String) {
    NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION)
      .notify(project)
  }

  fun notifyWarning(project: Project?, content: String) {
    NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING)
      .notify(project)
  }

  fun notifyError(project: Project?, content: String) {
    NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR)
      .notify(project)
  }
}