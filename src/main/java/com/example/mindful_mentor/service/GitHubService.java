package com.example.mindful_mentor.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;

@Service
public class GitHubService {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/ArtisTechs/image-uploads/contents/uploads";
    private static final String GITHUB_TOKEN = "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";  // Replace with your GitHub token

    public String uploadImage(MultipartFile file, String imageName) throws Exception {
        if (GITHUB_TOKEN == null || GITHUB_TOKEN.isEmpty()) {
            throw new IllegalArgumentException("GitHub token is missing. Please provide a valid token.");
        }

        // Convert image to Base64
        String encodedImage = Base64.getEncoder().encodeToString(file.getBytes());

        // Prepare the request body
        String filePath = "uploads/" + imageName;  // Path to upload in the repository
        String commitMessage = "Add profile picture";  // Commit message for GitHub

        // Check if the file already exists and get the sha (if updating the file)
        String sha = getFileSha(filePath);

        // Prepare the GitHub API request payload
        String payload = "{"
                + "\"message\": \"" + commitMessage + "\","
                + "\"content\": \"" + encodedImage + "\","
                + (sha != null ? "\"sha\": \"" + sha + "\"," : "")  // Include sha if the file already exists
                + "\"branch\": \"main\""  // Specify the branch name
                + "}";

        // Set headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + GITHUB_TOKEN);  // Use Bearer token instead of 'token'
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up the HTTP entity with the payload and headers
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        // Make the PUT request to the GitHub API to upload the image
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(
                    GITHUB_API_URL,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            // Handle common HTTP errors (Unauthorized, Not Found, etc.)
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Authorization error: Invalid or expired GitHub token.");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new RuntimeException("API rate limit exceeded. Please try again later.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("File not found: " + e.getResponseBodyAsString());
            } else {
                throw new RuntimeException("GitHub API error: " + e.getResponseBodyAsString());
            }
        } catch (RestClientException e) {
            // Handle any other RestTemplate exceptions
            throw new RuntimeException("Error communicating with GitHub: " + e.getMessage());
        }

        // Check if the upload was successful (status 201 Created)
        if (response.getStatusCode() == HttpStatus.CREATED) {
            // Return the URL to access the uploaded image from GitHub (raw URL)
            return "https://raw.githubusercontent.com/ArtisTechs/image-uploads/main/" + filePath;
        } else {
            throw new RuntimeException("Failed to upload image to GitHub: " + response.getBody());
        }
    }

    private String getFileSha(String filePath) {
        try {
            // Make a GET request to retrieve the file metadata (including sha)
            String url = GITHUB_API_URL + "/" + filePath;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + GITHUB_TOKEN);  // Use Bearer token for better security
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            // Extract sha from the response body
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("sha")) {
                return responseBody.get("sha").toString();
            }

        } catch (HttpClientErrorException.NotFound e) {
            // If the file is not found, return null (indicating it's a new file)
            return null;
        } catch (HttpClientErrorException e) {
            // Handle other GitHub API errors
            throw new RuntimeException("Failed to retrieve file SHA from GitHub: " + e.getResponseBodyAsString());
        }
        return null;
    }
}
