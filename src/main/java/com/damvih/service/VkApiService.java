package com.damvih.service;

import com.damvih.dto.api.VkApiPhotoResponse;
import com.damvih.dto.api.VkApiResponseContent;
import com.damvih.dto.api.error.VkApiErrorResponseContent;
import com.damvih.exception.ExternalApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class VkApiService {

    private static final String MAIN_URL = "https://api.vk.com/method/";
    private static final String VERSION = "5.199";
    private static final String PHOTO_TYPE = "photo";
    private static final String SORT_METHOD = "id_asc";
    private static final int COUNT = 1000;
    private static final int START_OFFSET = 0;
    private static final int DELAY_TIME = 1000;
    public static final int TOO_MANY_REQUESTS_VK_CODE = 6;

    @Value("${VK_TOKEN}")
    private String TOKEN;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public List<Long> getGroupMemberIds(Long groupId) {
        String methodName = "groups.getMembers";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("group_id", String.valueOf(groupId));
        params.add("count", String.valueOf(COUNT));
        params.add("offset", String.valueOf(START_OFFSET));
        params.add("sort", SORT_METHOD);

        try {
            return mapper.convertValue(
                    getAllItems(methodName, params),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception exception) {
            throw new ExternalApiException(exception.getMessage());
        }
    }

    public List<VkApiPhotoResponse> getPhotoIds(Long groupId, Long albumId) {
        String methodName = "photos.get";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("owner_id", "-" + groupId);
        params.add("album_id", String.valueOf(albumId));
        params.add("count", String.valueOf(COUNT));
        params.add("offset", String.valueOf(START_OFFSET));
        params.add("rev", "0");

        try {
            return mapper.convertValue(
                    getAllItems(methodName, params),
                    new TypeReference<>() {}
            );
        } catch (Exception exception) {
            throw new ExternalApiException(exception.getMessage());
        }
    }

    public List<Long> getLikeIds(Long groupId, Long photoId) {
        String methodName = "likes.getList";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("owner_id", "-" + groupId);
        params.add("item_id", String.valueOf(photoId));
        params.add("type", PHOTO_TYPE);
        params.add("count", String.valueOf(COUNT));
        params.add("offset", String.valueOf(START_OFFSET));

        try {
            return mapper.convertValue(
                    getAllItems(methodName, params),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception exception) {
            throw new ExternalApiException(exception.getMessage());
        }
    }

    private List<JsonNode> getAllItems(String methodName, MultiValueMap<String, String> params) throws IOException, InterruptedException {
        VkApiResponseContent vkApiResponseContent = getVkApiResponseContent(methodName, params);

        List<JsonNode> items = vkApiResponseContent.getItems();

        for (int i = COUNT; i < vkApiResponseContent.getCount(); i += COUNT) {
            params.set("offset", String.valueOf(i));
            vkApiResponseContent = getVkApiResponseContent(methodName, params);
            items.addAll(vkApiResponseContent.getItems());
        }
        return items;
    }

    private VkApiResponseContent getVkApiResponseContent(String methodName, MultiValueMap<String, String> params) throws IOException, InterruptedException {
        URI uri = buildUri(methodName, params);
        HttpRequest request = buildRequest(uri);
        JsonNode responseNode = getResponseNode(request);

        if (isError(responseNode)) {
            VkApiErrorResponseContent errorResponseContent = mapToVkApiErrorResponseContent(responseNode);

            if (isTooManyRequestsError(errorResponseContent.getCode())) {
                Thread.sleep(DELAY_TIME);
                responseNode = getResponseNode(request);
            } else {
                throw new ExternalApiException(errorResponseContent.getMessage());
            }

        }

        return mapper.treeToValue(responseNode.get("response"), VkApiResponseContent.class);
    }

    private VkApiErrorResponseContent mapToVkApiErrorResponseContent(JsonNode responseNode) throws JsonProcessingException {
        return mapper.treeToValue(
                responseNode.get("error"),
                VkApiErrorResponseContent.class
        );
    }

    private JsonNode getResponseNode(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    private boolean isError(JsonNode responseNode) {
        return responseNode.get("response") == null;
    }

    private boolean isTooManyRequestsError(int errorCode) {
        return errorCode == TOO_MANY_REQUESTS_VK_CODE;
    }

    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
    }

    private URI buildUri(String methodName, MultiValueMap<String, String> params) {
        return UriComponentsBuilder
                .fromHttpUrl(MAIN_URL + methodName)
                .queryParam("access_token", TOKEN)
                .queryParam("v", VERSION)
                .queryParams(params)
                .build()
                .toUri();
    }

}