package com.example.myapplication;

import java.util.List;

public class GeminiResponse {
    private List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }
}

class Candidate {
    private Content content;

    public Content getContent() {
        return content;
    }
}