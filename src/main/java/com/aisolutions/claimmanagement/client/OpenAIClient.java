package com.aisolutions.claimmanagement.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * OpenAI REST Client — supports both text and vision (image) messages.
 */
@RegisterRestClient(configKey = "openai-api")
@Path("/v1/chat/completions")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${openai.api.key}")
public interface OpenAIClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<OpenAIResponse> chat(OpenAIRequest request);

    // ── Request ──────────────────────────────────────────────────────────
    class OpenAIRequest {
        public String model = "gpt-4o-mini";
        public List<Message> messages;
        public double temperature = 0.1;   // low temp for structured extraction
        public int max_tokens = 512;

        public OpenAIRequest() {}
        public OpenAIRequest(List<Message> messages) { this.messages = messages; }

        /** Text-only message */
        public static Message textMessage(String role, String text) {
            Message m = new Message();
            m.role = role;
            m.content = text;
            return m;
        }

        /** Vision message — content is an array of parts */
        public static Message visionMessage(String role, String text, String base64Image, String mimeType) {
            Message m = new Message();
            m.role = role;
            ContentPart textPart = new ContentPart();
            textPart.type = "text";
            textPart.text = text;

            ContentPart imagePart = new ContentPart();
            imagePart.type = "image_url";
            imagePart.image_url = new ImageUrl("data:" + mimeType + ";base64," + base64Image);

            m.contentParts = List.of(textPart, imagePart);
            return m;
        }

        public static class Message {
            public String role;
            // For text-only: use content string
            public String content;
            // For vision: use contentParts (serialised as "content" array)
            @com.fasterxml.jackson.annotation.JsonIgnore
            public List<ContentPart> contentParts;

            @com.fasterxml.jackson.annotation.JsonProperty("content")
            public Object getContentForJson() {
                return contentParts != null ? contentParts : content;
            }
        }

        public static class ContentPart {
            public String type;
            public String text;
            public ImageUrl image_url;
        }

        public static class ImageUrl {
            public String url;
            public ImageUrl() {}
            public ImageUrl(String url) { this.url = url; }
        }
    }

    // ── Response ─────────────────────────────────────────────────────────
    class OpenAIResponse {
        public String id;
        public List<Choice> choices;

        public String getContent() {
            if (choices != null && !choices.isEmpty() && choices.get(0).message != null) {
                return choices.get(0).message.content;
            }
            return null;
        }

        public static class Choice {
            public int index;
            public Message message;
            public String finish_reason;

            public static class Message {
                public String role;
                public String content;
            }
        }
    }
}
