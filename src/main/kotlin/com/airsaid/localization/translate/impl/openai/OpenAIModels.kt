package com.airsaid.localization.translate.impl.openai

import com.google.gson.annotations.SerializedName

data class OpenAIRequest(
  var model: String,
  var messages: List<OpenAIMessage>
)

data class OpenAIMessage(
  var role: String,
  var content: String
)

data class OpenAIModelsResponse(
  @SerializedName("data")
  val data: List<OpenAIModel> = emptyList()
)

data class OpenAIModel(
  @SerializedName("id")
  val id: String = ""
)
