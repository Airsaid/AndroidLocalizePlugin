package com.airsaid.localization.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.jewel.bridge.JewelComposePanel
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

  protected open val defaultPreferredSize: Pair<Int, Int>? = null

  private val composePanel by lazy {
    JewelComposePanel(config = {
      defaultPreferredSize?.let { preferredSize = Dimension(it.first, it.second) }
    }, content = {
      Content()
    })
  }
  private var onClickOKCallback: (() -> Unit)? = null

  override fun createCenterPanel(): JComponent = composePanel

  init {
    init()
  }

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
