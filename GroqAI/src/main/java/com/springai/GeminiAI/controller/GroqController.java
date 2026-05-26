package com.springai.GeminiAI.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class GroqController {

    private final ChatClient chatClient;

    MessageWindowChatMemory memory =
            MessageWindowChatMemory.builder()
                    .chatMemoryRepository(new InMemoryChatMemoryRepository())
                    .build();

    public GroqController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }

    @GetMapping("/api/groq/ask")
    public String askQuestion(@RequestParam String question) {

        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    @PostMapping("/api/recommend")
    public String recommend(@RequestParam String type, @RequestParam String year, @RequestParam String lang) {

        String tempt = """
                    I want to watch a {type} movie tonight with good rating,
                    looking for movies around this year {year}.
                    The language I'm looking for is {lang}.
                    Suggest one specific movie and tell me the cast and length of the movie.
                    
                    Response format should be:
                    1.Movie name
                    2.Basic plot
                    3.Cast
                    4.Length
                    5.IMDB rating 
                """;

        PromptTemplate promptTemplate = new PromptTemplate(tempt);
        Prompt prompt = promptTemplate.create(Map.of("type", type, "year", year, "lang", lang));

        String response = chatClient
                .prompt(prompt)
                .call()
                .content();

        return response;
    }
}
