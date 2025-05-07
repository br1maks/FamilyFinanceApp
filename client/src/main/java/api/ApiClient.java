package api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.YearMonth;
import java.util.List;

class ErrorResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

public class ApiClient {
    private static final ApiClient instance = new ApiClient();
    private final String baseUrl = "http://localhost:8080/api/v1";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;
    private String refreshToken;

    public static ApiClient getInstance() {
        return instance;
    }

    private ApiClient() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void login(LoginDTO dto) throws Exception {
        ResponseWrapper<UserDTO> response = post("/auth/login", dto, new TypeReference<>() {}, false);
        this.accessToken = (String) response.getAdditionalFields().get("accessToken");
        this.refreshToken = (String) response.getAdditionalFields().get("refreshToken");
    }

    public void register(RegisterDTO dto) throws Exception {
        post("/auth/register", dto, new TypeReference<ResponseWrapper<UserDTO>>() {}, false);
    }

    public void logout() {
        accessToken = null;
        refreshToken = null;
    }

    public UserDTO getMeInfo() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/me"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<UserDTO>>() {}).getData();
    }

    public List<FamilyDTO> getFamilies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/me/families"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<FamilyDTO>>>() {}).getData();
    }

    public FamilyDTO createFamily(CreateFamilyDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<FamilyDTO>>() {}).getData();
    }

    public FamilyDTO getFamily(long familyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families/" + familyId))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<FamilyDTO>>() {}).getData();
    }

    public String getInviteCode(long familyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families/" + familyId + "/invite-code"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<String>>() {}).getData();
    }

    public FamilyDTO joinFamilyByCode(JoinByInviteCodeDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families/join"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<FamilyDTO>>() {}).getData();
    }

    public FamilyDTO kickMember(long familyId, KickMemberDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families/" + familyId + "/kick"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<FamilyDTO>>() {}).getData();
    }

    public FamilyMemberDTO setMemberRole(long familyId, long memberId, SetFamilyMemberRoleDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families/" + familyId + "/members/" + memberId + "/role"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<FamilyMemberDTO>>() {}).getData();
    }

    public List<BudgetDTO> getBudget(long familyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/budgets/" + familyId))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<BudgetDTO>>>() {}).getData();
    }

    public void setBudgetLimit(long familyId, BudgetLimitRequestDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/budgets/" + familyId + "/limit"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    public List<CategoryDTO> getCategories(long familyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/categories?familyId=" + familyId))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<CategoryDTO>>>() {}).getData();
    }

    public CategoryDTO createCategory(CreateCategoryDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/categories"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<CategoryDTO>>() {}).getData();
    }

    public void deleteCategory(long categoryId, DeleteCategoryDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/categories/" + categoryId))
                .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .method("DELETE", HttpRequest.BodyPublishers.ofString(json))
                        .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    public void updateCategory(long categoryId, UpdateCategoryDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/categories/" + categoryId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    public List<TransactionDTO> getTransactions(long familyId, YearMonth period) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/transactions?familyId=" + familyId + "&period=" + period))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<TransactionDTO>>>() {}).getData();
    }

    public TransactionDTO createTransaction(CreateTransactionDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/transactions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<TransactionDTO>>() {}).getData();
    }

    public List<GoalDTO> getGoals(long familyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/" + familyId + "/goals"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<GoalDTO>>>() {}).getData();
    }

    public GoalDTO createGoal(long familyId, CreateGoalDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/" + familyId + "/goals"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<GoalDTO>>() {}).getData();
    }

    public GoalDTO updateGoal(long familyId, long goalId, UpdateGoalDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/" + familyId + "/goals/" + goalId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<GoalDTO>>() {}).getData();
    }

    public FamilyDTO updateFamily(long familyId, UpdateFamilyDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/families/" + familyId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<FamilyDTO>>() {}).getData();
    }

    public List<UserDTO> getAllUsers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/admin/users"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<UserDTO>>>() {}).getData();
    }

    public void banUser(long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/admin/users/ban/" + userId))
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    public void unbanUser(long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/admin/users/unban/" + userId))
                .header("Authorization", "Bearer " + accessToken)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    public void deleteUser(long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/admin/users/" + userId))
                .header("Authorization", "Bearer " + accessToken)
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    public List<FamilyDTO> findAllFamilies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/admin/families"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendRequestWithRetry(request, new TypeReference<ResponseWrapper<List<FamilyDTO>>>() {}).getData();
    }

    public void deleteFamily(long familyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/admin/families/" + familyId))
                .header("Authorization", "Bearer " + accessToken)
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        sendRequestWithRetry(request, new TypeReference<ResponseWrapper<Void>>() {});
    }

    private void refreshAccessToken() throws Exception {
        if (refreshToken == null) {
            throw new IllegalStateException("Refresh token is not available. Please log in again.");
        }
        RefreshAccessTokenDTO dto = new RefreshAccessTokenDTO();
        dto.setRefreshToken(refreshToken);
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/auth/refresh"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        ResponseWrapper<String> response = sendRequest(request, new TypeReference<ResponseWrapper<String>>() {});
        this.accessToken = response.getData();
    }

    private <T> ResponseWrapper<T> post(String endpoint, Object requestBody,
                                        TypeReference<ResponseWrapper<T>> typeRef, boolean useAuth) throws Exception {
        String json = objectMapper.writeValueAsString(requestBody);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + endpoint))
                .header("Content-Type", "application/json");
        if (useAuth) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(json)).build();
        return sendRequestWithRetry(request, typeRef);
    }

    private <T> ResponseWrapper<T> sendRequestWithRetry(HttpRequest request,
                                                        TypeReference<ResponseWrapper<T>> typeRef) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            refreshAccessToken();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(request.uri())
                    .headers(request.headers().map().entrySet().stream()
                            .flatMap(entry -> entry.getValue().stream()
                                    .map(value -> new String[]{entry.getKey(), value}))
                            .toArray(String[]::new));
            if (request.method().equals("POST") || request.method().equals("PATCH") || request.method().equals("DELETE")) {
                builder.method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));
            } else {
                builder.GET();
            }
            builder.setHeader("Authorization", "Bearer " + accessToken);
            request = builder.build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() >= 400) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception("API error: " + (error.getMessage() != null ? error.getMessage() : response.body()));
            } catch (Exception e) {
                throw new Exception("API error: " + response.body());
            }
        }
        return objectMapper.readValue(response.body(), typeRef);
    }

    private <T> ResponseWrapper<T> sendRequest(HttpRequest request, TypeReference<ResponseWrapper<T>> typeRef) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception("API error: " + (error.getMessage() != null ? error.getMessage() : response.body()));
            } catch (Exception e) {
                throw new Exception("API error: " + response.body());
            }
        }
        return objectMapper.readValue(response.body(), typeRef);
    }
}