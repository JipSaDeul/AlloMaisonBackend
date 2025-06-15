package com.example.allomaison.Controllers;

import com.example.allomaison.DTOs.*;
import com.example.allomaison.DTOs.Requests.*;
import com.example.allomaison.DTOs.Responses.ConversationResponse;
import com.example.allomaison.DTOs.Responses.ErrorResponse;
import com.example.allomaison.DTOs.Responses.SuccessResponse;
import com.example.allomaison.Entities.ProviderInfo;
import com.example.allomaison.Entities.Task;
import com.example.allomaison.Mapper.ProviderInfoMapper;
import com.example.allomaison.Repositories.CategoryRepository;
import com.example.allomaison.Security.JwtService;
import com.example.allomaison.Services.*;
import com.example.allomaison.Utils.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;
    private final ProviderService providerService;
    private final TaskService taskService;
    private final OrderService orderService;
    private final UserService userService;
    private final ProviderApplicationService providerApplicationService;
    private final CategoryRepository categoryRepository;
    private final ReviewService reviewService;
    private final ConversationService conversationService;
    private final NoticeService noticeService;

    private Optional<UserDTO> extractUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring(7);
        return jwtService.parseToken(token);
    }

    private ResponseEntity<?> validateProviderAndExtractUser(String authHeader, java.util.function.Consumer<UserDTO> onSuccess) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        UserDTO user = userOpt.get();
        if (!providerService.isProvider(user.getUserId())) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("Only providers can access this")
                            .build());
        }

        onSuccess.accept(user);
        return null; // null means success, actual logic will be run in caller
    }


    @GetMapping("/tasks")
    public ResponseEntity<?> getNearbyTasks(@RequestHeader("Authorization") String authHeader) {
        final ResponseEntity<?>[] response = new ResponseEntity<?>[1];

        ResponseEntity<?> error = validateProviderAndExtractUser(authHeader, user -> {
            Long userId = user.getUserId();

            Optional<ProviderInfo> infoOpt = providerService.getProviderInfo(userId);
            if (infoOpt.isEmpty()) {
                response[0] = ResponseEntity.status(404).body(ErrorResponse.builder()
                        .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                        .message("Provider info not found")
                        .build());
                return;
            }

            ProviderInfo info = infoOpt.get();
            Integer zipcode = info.getCityZipcode();
            Integer catId = info.getCatId();

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp oneMonthLater = new Timestamp(now.getTime() + 30L * 24 * 60 * 60 * 1000);

            List<TaskDTO> tasks = taskService.getNearbyTasks(zipcode, catId, now, oneMonthLater);
            response[0] = ResponseEntity.ok(tasks);
        });

        return error != null ? error : response[0];
    }


    @PostMapping(value = "/provider/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> applyAsProvider(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute ProviderApplicationRequest request
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        UserDTO user = userOpt.get();
        Long userId = user.getUserId();

        if (providerService.isProvider(userId)) {
            return ResponseEntity.status(409).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_ALREADY_REGISTERED)
                            .message("You are already a provider")
                            .build());
        }

        request.setUserId(userId); // overwrite whatever is in the form
        return providerApplicationService.registerApplication(request)
                .<ResponseEntity<?>>map(result -> ResponseEntity.ok(new SuccessResponse()))
                .orElseGet(() -> ResponseEntity.status(400).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                                .message("Invalid city, category, or duplicate pending application")
                                .build()));
    }

    @PostMapping(value = "/post-task", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> rawRequest
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        UserDTO user = userOpt.get();

        String title = (String) rawRequest.get("title");
        String categoryName = (String) rawRequest.get("category");
        String frequencyStr = (String) rawRequest.get("frequency");
        String zipcodeStr = (String) rawRequest.get("zipcode");

        if (title == null || categoryName == null || frequencyStr == null || zipcodeStr == null) {
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Missing required fields")
                            .build());
        }

        var categoryOpt = categoryRepository.findByName(categoryName);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid category")
                            .build());
        }

        int zipcode;
        try {
            zipcode = Integer.parseInt(zipcodeStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid zipcode format")
                            .build());
        }

        TaskRequest request = new TaskRequest();
        request.setCustomerId(user.getUserId());
        request.setTitle(title);
        request.setCatId(categoryOpt.get().getCatId());
        request.setCityZipcode(zipcode);

        try {
            request.setFrequency(Task.Frequency.valueOf(frequencyStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid frequency")
                            .build());
        }

        request.setAddress((String) rawRequest.get("address"));
        request.setCustomerContact((String) rawRequest.get("customerContact"));
        request.setDescription((String) rawRequest.get("description"));

        try {
            request.setStartTime(Timestamp.valueOf((String) rawRequest.get("startTime")));
            request.setEndTime(Timestamp.valueOf((String) rawRequest.get("endTime")));
            request.setBudget(Integer.parseInt((String) rawRequest.get("budget")));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid time or budget format")
                            .build());
        }

        return taskService.createTask(request)
                .<ResponseEntity<?>>map(task -> ResponseEntity.ok(new SuccessResponse()))
                .orElseGet(() -> ResponseEntity.status(500).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.SERVER_ERROR)
                                .message("Failed to create task")
                                .build()));

    }

    @GetMapping("/tasks/my")
    public ResponseEntity<?> getMyTasks(@RequestHeader("Authorization") String authHeader) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long userId = userOpt.get().getUserId();
        List<TaskDTO> tasks = taskService.getTasksByCustomerId(userId);
        return ResponseEntity.ok(tasks);
    }
    @GetMapping("/orders/my")
    public ResponseEntity<?> getMyOrders(@RequestHeader("Authorization") String authHeader) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long customerId = userOpt.get().getUserId();
        List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/provider")
    public ResponseEntity<?> getMyProviderOrders(@RequestHeader("Authorization") String authHeader) {
        final ResponseEntity<?>[] response = new ResponseEntity<?>[1];

        ResponseEntity<?> error = validateProviderAndExtractUser(authHeader, user -> {
            Long providerId = user.getUserId();
            List<OrderDTO> orders = orderService.getOrdersByProviderId(providerId);
            response[0] = ResponseEntity.ok(orders);
        });

        return error != null ? error : response[0];
    }

    @PostMapping("/order/change-status")
    public ResponseEntity<?> changeOrderStatus(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("status") String statusStr,
            @RequestParam("orderId") Long orderId
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long userId = userOpt.get().getUserId();

        Task.Status newStatus;
        try {
            newStatus = Task.Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid task status: " + statusStr)
                            .build());
        }

        boolean success = orderService.changeOrderStatus(orderId, userId, newStatus);
        if (!success) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("Unauthorized to update this task status or task not found")
                            .build());
        }

        return ResponseEntity.ok(new SuccessResponse());
    }

    @PostMapping("/review")
    public ResponseEntity<?> reviewMyOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReviewRequest request
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long userId = userOpt.get().getUserId();

        boolean success = reviewService.submitReview(userId, request);
        if (!success) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("You can only review completed orders of your own")
                            .build());
        }

        return ResponseEntity.ok(new SuccessResponse());
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        return ResponseEntity.ok(userOpt.get());
    }

    @PostMapping(value = "/userInfo/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId,
            @RequestParam("avatar") MultipartFile avatar
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        UserDTO user = userOpt.get();

        if (!user.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("You can only upload your own avatar")
                            .build());
        }

        if (avatar == null || avatar.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Missing or empty avatar file")
                            .build());
        }

        var result = FileStorageUtil.saveAvatarFile(avatar, userId);
        if (!result.isSuccessful()) {
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message(result.getError())
                            .build());
        }

        user.setAvatarUrl(result.getUrl());
        boolean success = userService.updateProfile(user);
        if (!success) {
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.SERVER_ERROR)
                            .message("Failed to update user avatar")
                            .build());
        }

        return ResponseEntity.ok(Map.of("avatarUrl", result.getUrl()));
    }

    @PatchMapping(value = "/userInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserUpdateRequest updateRequest
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        UserDTO user = getUserDTO(updateRequest, userOpt);

        boolean success = userService.updateProfile(user);
        if (!success) {
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.SERVER_ERROR)
                            .message("Failed to update user profile")
                            .build());
        }

        return ResponseEntity.ok(new SuccessResponse());
    }

    private static UserDTO getUserDTO(UserUpdateRequest updateRequest, Optional<UserDTO> userOpt) {
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("UserDTO is required");
        }

        UserDTO user = userOpt.get();

        if (updateRequest.getUserFirstName() != null) {
            user.setUserFirstName(updateRequest.getUserFirstName());
        }

        if (updateRequest.getUserLastName() != null) {
            user.setUserLastName(updateRequest.getUserLastName());
        }

        if (updateRequest.getGender() != null) {
            user.setGender(updateRequest.getGender());
        }

        if (updateRequest.getBirthDate() != null) {
            user.setBirthDate(updateRequest.getBirthDate());
        }

        return user;
    }

    @PostMapping("/user/update-username")
    public ResponseEntity<?> updateUsername(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUsernameRequest request
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        String newUserName = request.getNewUserName();
        if (newUserName == null || newUserName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Username cannot be empty")
                            .build());
        }

        Long userId = userOpt.get().getUserId();
        boolean updated = userService.updateUserName(userId, newUserName);

        if (!updated) {
            return ResponseEntity.status(409).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_DUPLICATE)
                            .message("Username already taken")
                            .build());
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Username updated, please re-login"
        ));
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getMyConversations(@RequestHeader("Authorization") String authHeader) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long userId = userOpt.get().getUserId();
        List<ConversationResponse> conversations = conversationService.getConversationResponsesForUser(
                userId,
                otherUserId -> userService.getUserById(otherUserId)
                        .map(UserDTO::getUserName)
                        .orElse("Unknown")
        );

        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/chat-messages")
    public ResponseEntity<?> getMessages(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("chatId") Long chatId
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        List<ChatMessageDTO> messages = conversationService.getMessagesByChatId(chatId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/chat-messages")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MessageRequest request
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty() || !userOpt.get().getUserId().equals(request.senderId())) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Invalid user or token mismatch")
                            .build());
        }

        ChatMessageDTO result = conversationService.sendMessage(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createOrGetConversation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MessageRequest request
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty() || !userOpt.get().getUserId().equals(request.senderId())) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Invalid user or token mismatch")
                            .build());
        }

        Long senderId = request.senderId();
        Long receiverId = request.receiverId();
        Long conversationId = conversationService.getOrCreateConversationId(senderId, receiverId);

        return ResponseEntity.ok(new java.util.HashMap<>() {{
            put("conversationId", conversationId);
        }});
    }

    @PostMapping("/orders/accept")
    public ResponseEntity<?> acceptOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("taskId") Long taskId
    ) {
        final ResponseEntity<?>[] response = new ResponseEntity<?>[1];

        ResponseEntity<?> error = validateProviderAndExtractUser(authHeader, user -> {
            Long providerId = user.getUserId();
            boolean success = orderService.createOrderIfEligible(taskId, providerId);

            if (!success) {
                response[0] = ResponseEntity.status(400).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                                .message("Task not available or provider not eligible")
                                .build()
                );
            } else {
                response[0] = ResponseEntity.ok(new SuccessResponse());
            }
        });

        return error != null ? error : response[0];
    }

    @GetMapping("/provider/me")
    public ResponseEntity<?> getMyProviderInfo(@RequestHeader("Authorization") String authHeader) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long providerId = userOpt.get().getUserId();

        if (!providerService.isProvider(providerId)) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("You are not a provider")
                            .build());
        }

        Optional<ProviderInfo> infoOpt = providerService.getProviderInfo(providerId);
        if (infoOpt.isEmpty()) {
            return ResponseEntity.status(404).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                            .message("Provider info not found")
                            .build());
        }

        ProviderInfo info = infoOpt.get();

        List<String> labelNames = providerService.getProviderLabels(providerId);

        ProviderInfoDTO dto = ProviderInfoMapper.toDTO(info, labelNames);

        String categoryName = providerService.getCategoryNameById(info.getCatId()).orElse("Unknown");
        String cityName = providerService.getCityNameByZip(info.getCityZipcode()).orElse("Unknown");
        String providerName = userOpt.get().getUserName();

        return getResponseEntity(dto, categoryName, cityName, providerName);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderInfoById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("providerId") Long providerId
    ) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        if (!providerService.isProvider(providerId)) {
            return ResponseEntity.status(404).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                            .message("Provider not found")
                            .build());
        }

        Optional<ProviderInfo> infoOpt = providerService.getProviderInfo(providerId);
        if (infoOpt.isEmpty()) {
            return ResponseEntity.status(404).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                            .message("Provider info not found")
                            .build());
        }

        ProviderInfo info = infoOpt.get();

        List<String> labelNames = providerService.getProviderLabels(providerId);
        ProviderInfoDTO dto = ProviderInfoMapper.toDTO(info, labelNames);

        String categoryName = providerService.getCategoryNameById(info.getCatId()).orElse("Unknown");
        String cityName = providerService.getCityNameByZip(info.getCityZipcode()).orElse("Unknown");
        String providerName = providerService.getProviderName(providerId).orElse("Unknown");

        return getResponseEntity(dto, categoryName, cityName, providerName);
    }

    private ResponseEntity<?> getResponseEntity(ProviderInfoDTO dto, String categoryName, String cityName, String providerName) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", dto.getProviderId());
        response.put("providerId", dto.getProviderId());
        response.put("category", categoryName);
        response.put("city", cityName);
        response.put("providerName", providerName);
        response.put("description", dto.getDescription());
        response.put("servicesOffered", dto.getServiceOffered());
        response.put("serviceArea", dto.getServiceArea());
        response.put("providerLabels", dto.getLabels());
        response.put("priceRange", dto.getPriceRange());

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/provider/me")
    public ResponseEntity<?> updateProviderInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProviderUpdateRequest request
    ) {
        Optional<UserDTO> userOpt;
        userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        Long providerId = userOpt.get().getUserId();
        if (!providerService.isProvider(providerId)) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("You are not a provider")
                            .build());
        }

        // Validate category
        var catOpt = categoryRepository.findByName(request.getCategory());
        if (catOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid category name")
                            .build());
        }

        // Validate city (zipcode)
        int zipcode;
        try {
            zipcode = Integer.parseInt(request.getCity());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid city/zipcode")
                            .build());
        }

        if (providerService.getCityNameByZip(zipcode).isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Zipcode does not exist")
                            .build());
        }

        // Update provider name
        String newName = request.getProviderName();
        if (newName != null && !newName.trim().isEmpty()) {
            boolean updated = userService.updateUserName(providerId, newName.trim());
            if (!updated) {
                return ResponseEntity.status(409).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_DUPLICATE)
                                .message("Provider name already exists")
                                .build());
            }
        }

        ProviderInfoDTO dto = new ProviderInfoDTO();
        dto.setProviderId(providerId);
        dto.setDescription(request.getDescription());
        dto.setServiceOffered(request.getServicesOffered());
        dto.setCityZipcode(zipcode);
        dto.setServiceArea(request.getServiceArea());
        dto.setPriceRange(request.getPriceRange());
        dto.setLabels(request.getProviderLabels());

        boolean success = providerService.updateProviderInfo(dto);
        if (!success) {
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.SERVER_ERROR)
                            .message("Failed to update provider info")
                            .build());
        }

        return ResponseEntity.ok(new SuccessResponse());
    }

    @GetMapping("/notices")
    public ResponseEntity<?> getMyNotices(@RequestHeader("Authorization") String authHeader) {
        Optional<UserDTO> userOpt = extractUserFromToken(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid Authorization header")
                            .build());
        }

        UserDTO user = userOpt.get();

        List<Pair<NoticeMessageDTO, Boolean>> noticePairs = noticeService.getNoticesForUser(user);

        List<Map<String, Object>> responseList = noticePairs.stream()
                .map(pair -> {
                    NoticeMessageDTO notice = pair.getFirst();
                    Boolean recent = pair.getSecond();

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("noticeId", notice.noticeId());
                    entry.put("userId", notice.userId());
                    entry.put("title", notice.title());
                    entry.put("content", notice.content());
                    entry.put("type", notice.type());
                    entry.put("targets", notice.targets());
                    entry.put("sentTime", notice.sentTime());
                    entry.put("recent", recent);

                    return entry;
                }).toList();

        return ResponseEntity.ok(responseList);
    }


}
