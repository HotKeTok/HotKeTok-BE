package com.hotketok.parser;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class OpenAIResponseParser {

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Root {
        public String model;
        public List<Output> output;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Output {
        public String type;      // "message"
        public List<Content> content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Content {
        public String type;      // "output_text" 만 관심
        public String text;
    }

    public record Parsed(String model, String text) {}

    public static Parsed parse(String json) throws Exception {
        ObjectMapper om = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Root root = om.readValue(json, Root.class);

        String mergedText = (root.output == null ? List.<Output>of() : root.output)
                .stream()
                .filter(o -> o.content != null)
                .flatMap(o -> o.content.stream())
                .filter(c -> "output_text".equals(c.type))
                .map(c -> c.text)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.joining("\n"));

        return new Parsed(root.model, mergedText);
    }
}
