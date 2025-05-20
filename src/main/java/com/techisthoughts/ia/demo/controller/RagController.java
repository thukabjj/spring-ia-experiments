package com.techisthoughts.ia.demo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;


@RestController
public class RagController {

    private final Logger LOG = LoggerFactory.getLogger(RagController.class);

    private final ChatClient chatClient;

    public RagController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .system("You are a helpful assistant.")
                .user("Hello, how are you?")
                .call()
                .content();
    }

    @PostMapping("/classify")
    public String classify() {

        return "";
    }

}
