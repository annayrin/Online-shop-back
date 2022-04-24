package com.example.fashionshop.controller;

import com.example.fashionshop.model.Product;
import com.example.fashionshop.model.User;
import com.example.fashionshop.model.dto.responseDto.ResponseDto;
import com.example.fashionshop.repository.UserRepository;
import com.example.fashionshop.service.UserService;
import com.example.fashionshop.validation.ProductValidator;
import com.example.fashionshop.validation.UserValidator;
import com.example.fashionshop.validation.ValidationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    /***
     *
     * @return returns the list of all registerd users
     */
    @GetMapping
    List<User> getAll(){
        return userService.getAll();
    }

    @GetMapping("user-id")
    ResponseEntity<ResponseDto> isUserExists(@RequestHeader String userId) {
        ResponseDto responseDto = new ResponseDto("user exists");
        responseDto.addInfo("exists", String.valueOf(userService.isExists(userId)));
        return ResponseEntity.ok(responseDto);
    }
}
