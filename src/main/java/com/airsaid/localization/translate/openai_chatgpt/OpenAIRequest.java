package com.airsaid.localization.translate.openai_chatgpt;

import java.util.List;

public class OpenAIRequest {
    private String model;
    private List<ChatGPTMessage> messages;

    public OpenAIRequest(String model, List<ChatGPTMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatGPTMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatGPTMessage> messages) {
        this.messages = messages;
    }
}
