package com.mathfusion.domain.explanation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatGPTRequest {

    private String model;
    private List<ChatMessage> messages;
    private int n;

    public ChatGPTRequest(String model, String prompt){
        this.model = model;
        this.messages = new ArrayList<ChatMessage>();
        this.messages.add(new ChatMessage(prompt));

        this.n = 1;
    }
}
