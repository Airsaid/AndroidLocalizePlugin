package com.airsaid.localization.translate

data class TranslatorCredentialDescriptor(
  val id: String,
  val label: String,
  val isSecret: Boolean = true,
  val required: Boolean = true,
  val description: String? = null,
  val placeholder: String? = null,
)
