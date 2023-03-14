package com.airsaid.localization.translate.openai_chatgpt

data class OpenAIResponse(
    val choices: List<Choice?>? = null,
    val created: Int? = null,
    val id: String? = null,
    val `object`: String? = null,
    val usage: Usage? = null
) {
    data class Choice(
        val finish_reason: String? = null,
        val index: Int? = null,
        val message: Message? = null
    ) {
        data class Message(
            val content: String? = null,
            val role: String? = null
        )
    }

    data class Usage(
        val completion_tokens: Int? = null,
        val prompt_tokens: Int? = null,
        val total_tokens: Int? = null
    )


    fun getTranslation() = choices?.firstOrNull()?.message?.content.orEmpty()

}