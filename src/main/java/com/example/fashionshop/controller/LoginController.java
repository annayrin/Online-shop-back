package com.example.fashionshop.controller;

import com.example.fashionshop.model.User;
import com.example.fashionshop.model.dto.responseDto.ResponseDto;
import com.example.fashionshop.service.UserService;
import com.example.fashionshop.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    @Autowired
    private UserService userService;

    /***
     *
     * @param user is made from the information provided by front-end that will be registered in database
     * @return responseDto to inform front-end that process has been done successfully/ failed
     */
    @PostMapping("/signup")
    ResponseEntity<ResponseDto> signUp(@RequestBody User user) {
        UserValidator.checkUserSignUp(user, HttpStatus.BAD_REQUEST, "user data is invalid to signUp");
        User login = userService.create(user);
        ResponseDto responseDto = new ResponseDto("User logged in.");
        responseDto.addInfo("UserId", String.valueOf(user));
        return ResponseEntity.ok(responseDto);
    }
}

