package com.appearnetworks.aiq;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public class AuthenticationResponse {
    private final String accessToken;

    private final JsonNode links;

    private final URI baseURL;

    public AuthenticationResponse(String accessToken, JsonNode links, URI baseURL) {
        this.accessToken = accessToken;
        this.links = links;
        this.baseURL = baseURL;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public URI getLink(String target) {
        return baseURL.resolve(links.path(target).textValue());
    }
}
