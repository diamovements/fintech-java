package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.request.SignInRequest;
import org.example.entity.request.SignUpRequest;
import org.example.entity.response.AuthenticationResponse;
import org.example.security.AuthenticationService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest request) {
        authenticationService.signUp(request);
    }

    @PostMapping("/signin")
    public AuthenticationResponse signIn(@RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token);
        return ResponseEntity.ok("Logout successful");
    }


}
