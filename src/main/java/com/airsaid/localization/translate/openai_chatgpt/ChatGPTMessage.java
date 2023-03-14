package com.airsaid.localization.translate.openai_chatgpt;

public class ChatGPTMessage {
    private String role;
    private String content;

    public ChatGPTMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
