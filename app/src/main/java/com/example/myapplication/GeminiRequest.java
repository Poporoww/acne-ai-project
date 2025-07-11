package com.example.myapplication;

import java.util.List;

public class GeminiRequest {
    private List<Content> contents;

    public GeminiRequest(List<Content> contents) {
        this.contents = contents;
    }

    public List<Content> getContents() {
        return contents;
    }
}

class Content {
    private List<Part> parts;

    public Content(List<Part> parts) {
        this.parts = parts;
    }

    public List<Part> getParts() {
        return parts;
    }
}

class Part {
    private String text;

    public Part(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}