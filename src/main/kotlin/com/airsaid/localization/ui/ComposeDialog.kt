package com.airsaid.localization.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.awt.ComposePanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import javax.swing.JComponent

/**
 * Base dialog wrapper that embeds Compose UI inside IntelliJ Swing dialogs.
 *
 * @author airsaid
 */
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

  protected open fun preferredSize(): Dimension? = null

  @Composable
  protected abstract fun Content()

  @Composable
  protected fun OnClickOK(callback: () -> Unit) {
    LaunchedEffect(callback) {
      onClickOKCallback = callback
    }
  }

  /**
   * Invokes the registered OK callback prior to delegating to the Swing dialog handler.
   */
  override fun doOKAction() {
    onClickOKCallback?.invoke()
    super.doOKAction()
  }
}
