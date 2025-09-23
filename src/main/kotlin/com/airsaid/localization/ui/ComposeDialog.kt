package com.airsaid.localization.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import javax.swing.JComponent

abstract class ComposeDialog(
  project: Project? = null,
  canBeParent: Boolean = true
) : DialogWrapper(project, canBeParent) {

  private val composePanel = ComposePanel()
  private var onOkAction: (() -> Unit)? = null

  init {
    preferredSize()?.let { composePanel.preferredSize = it }
    composePanel.setContent {
      IdeTheme {
        Content(
          onOkAction = { callback -> onOkAction = callback }
        )
      }
    }
    init()
  }

  override fun createCenterPanel(): JComponent = composePanel

  @Composable
  protected abstract fun Content(onOkAction: (callback: () -> Unit) -> Unit)

  protected open fun preferredSize(): Dimension? = null

  override fun doOKAction() {
    onOkAction?.invoke()
    super.doOKAction()
  }
}