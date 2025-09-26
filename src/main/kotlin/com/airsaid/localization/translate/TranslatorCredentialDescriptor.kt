package com.airsaid.localization.translate

/**
 * Descriptor of a credential field required by a translator.
 *
 * @author airsaid
 */
data class TranslatorCredentialDescriptor(
  val id: String,
  val label: String,
  val isSecret: Boolean = true,
  val required: Boolean = true,
  val description: String? = null,
  val placeholder: String? = null,
)
