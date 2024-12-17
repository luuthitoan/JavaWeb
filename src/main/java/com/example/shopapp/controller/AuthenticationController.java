package com.example.shopapp.controller;
import com.example.shopapp.component.LocalizationUtil;
import com.example.shopapp.dto.UserDTO;
import com.example.shopapp.dto.UserLoginDTO;
import com.example.shopapp.responses.LoginResponse;
import com.example.shopapp.responses.UserResponse;
import com.example.shopapp.services.AuthenticationService;
import com.example.shopapp.services.UserService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class    AuthenticationController {
    private  final AuthenticationService authenticationService;
    private final UserService userService;
    private final LocalizationUtil localizationUtil;
    @PostMapping("/outbound/authentication")
    ResponseEntity<?> outboundAuthenticate(@RequestParam("code") String code) throws Exception {
        var result = authenticationService.outboundAuthentication(code);
        return ResponseEntity.ok().body(result);
    }
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result)
    {
        try
        {
            if(result.hasErrors())
            {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword()))
            {
                return ResponseEntity.badRequest().body("Your retype password is not match");
            }
            return ResponseEntity.ok(UserResponse.fromUser(userService.createUser(userDTO)));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO)
    {
        try {
            String token = userService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword(), userLoginDTO.getRoleId());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            String message = localizationUtil.getLocalizedMessage(MessageKeys.LOGIN_SUCCESS);
            loginResponse.setMessage(message);
            return ResponseEntity.ok().body(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtil.getLocalizedMessage(MessageKeys.LOGIN_FAILED,e.getMessage())).build());
        }
    }
}
