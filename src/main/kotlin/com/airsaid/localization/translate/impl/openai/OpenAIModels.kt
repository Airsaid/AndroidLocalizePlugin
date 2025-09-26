package com.airsaid.localization.translate.impl.openai

import com.google.gson.annotations.SerializedName

/**
 * Request payload sent to the OpenAI chat completions endpoint.
 *
 * @author airsaid
 */
data class OpenAIRequest(
  var model: String,
  var messages: List<OpenAIMessage>
)

/**
 * Single message exchanged with the OpenAI chat API.
 *
 * @author airsaid
 */
data class OpenAIMessage(
  var role: String,
  var content: String
)

/**
 * Response wrapper returned when listing available OpenAI models.
 *
 * @author airsaid
 */
data class OpenAIModelsResponse(
  @SerializedName("data")
  val data: List<OpenAIModel> = emptyList()
)

/**
 * Model descriptor returned by the OpenAI catalog API.
 *
 * @author airsaid
 */
data class OpenAIModel(
  @SerializedName("id")
  val id: String = ""
)
