package com.example.shopapp.services;

import com.example.shopapp.dto.ExchangeTokenRequest;
import com.example.shopapp.dto.UserDTO;
import com.example.shopapp.model.User;
import com.example.shopapp.repositories.httpclient.OutboundIdentityClient;
import com.example.shopapp.repositories.httpclient.OutboundUserClient;
import com.example.shopapp.responses.LoginResponse;
import com.example.shopapp.responses.OutboundUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final OutboundIdentityClient outboundIdentityClient;
    private final OutboundUserClient outboundUserClient;
    private final UserService userService;

    @Value("${outbound.identity.client-id}")
    private String clientId;

    @Value("${outbound.identity.client-secret}")
    private String clientSecret;

    @Value("${outbound.identity.redirect-uri}")
    private String redirectUri;

    private final String grantType = "authorization_code";

    /**
     * Xử lý luồng xác thực thông qua Google OAuth2.
     *
     * @param code Mã xác thực từ Google
     * @return LoginResponse đối tượng chứa token và thông báo
     * @throws Exception nếu có lỗi xảy ra trong quá trình xử lý
     */
    public LoginResponse outboundAuthentication(String code) throws Exception {
        // Trao đổi mã xác thực lấy access token từ Google
        var tokenResponse = outboundIdentityClient.exchangeToken(
                ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .grantType(grantType)
                        .build()
        );

        // Lấy thông tin người dùng từ Google
        OutboundUserResponse userResponse = outboundUserClient.getUserInfo("json", tokenResponse.getAccessToken());

        // Kiểm tra người dùng đã tồn tại trong hệ thống chưa
        Optional<User> existingUser = userService.getUserByUserIdentifier(userResponse.getEmail());

        if (existingUser.isEmpty()) {
            // Nếu chưa tồn tại, tạo mới người dùng
            userService.createUser(
                    UserDTO.builder()
                            .userIdentifier(userResponse.getEmail())
                            .fullName(userResponse.getName())
                            .password("123456") // Mật khẩu mặc định, nên thay đổi
                            .googleAccountId(1) // Giả định 1 là mã Google
                            .roleId(1L) // Giả định 1L là mã Role mặc định
                            .build()
            );
        }

        // Đăng nhập người dùng và lấy token
        String token = userService.login(userResponse.getEmail(), "123456", 1L);

        // Trả về phản hồi đăng nhập
        return LoginResponse.builder()
                .token(token)
                .message("Login with Google successfully!")
                .build();
    }
}
