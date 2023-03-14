package com.airsaid.localization.translate.openai_chatgpt


data class OpenAIRequest(val model: String, val messages: List<ChatGPTMessage>)

data class ChatGPTMessage(val role: String? = null, val content: String? = null)