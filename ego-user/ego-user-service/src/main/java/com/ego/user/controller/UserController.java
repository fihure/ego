package com.ego.user.controller;

import com.ego.user.pojo.User;
import com.ego.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data,@PathVariable("type")Integer type){
        Boolean result=this.userService.checkData(data,type);
        if (result==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(result);
    }
    @PostMapping("/send")
    public ResponseEntity<Void> sendVerifyCode(String phone){
        Boolean result=this.userService.sendVerifyCode(phone);
        if (result==null||!result){
            return new  ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@Valid User user, @RequestParam("code")String code){
        Boolean result=this.userService.register(user,code);
        if (result==null||!result){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
