package com.airsaid.localization.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
  private var onClickOKCallback: (() -> Unit)? = null

  init {
    preferredSize()?.let { composePanel.preferredSize = it }
    composePanel.setContent {
      IdeTheme {
        Content()
      }
    }
    init()
  }

  override fun createCenterPanel(): JComponent = composePanel

  @Composable
  protected abstract fun Content()

  @Composable
  protected fun OnClickOK(callback: () -> Unit) {
    LaunchedEffect(callback) {
      onClickOKCallback = callback
    }
  }

  protected open fun preferredSize(): Dimension? = null

  override fun doOKAction() {
    onClickOKCallback?.invoke()
    super.doOKAction()
  }
}