package com.autotrader.backend.dto.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(
            max = 2000,
            message = "Message content must not exceed 2000 characters"
    )
    private String content;

    public CreateMessageRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}