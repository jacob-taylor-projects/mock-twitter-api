package com.cooksys.group_project_1_team_1.controllers;

import com.cooksys.group_project_1_team_1.models.CredentialDto;
import com.cooksys.group_project_1_team_1.models.TweetResponseDto;
import com.cooksys.group_project_1_team_1.models.UserResponseDto;
import com.cooksys.group_project_1_team_1.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getUserFeed(@PathVariable("username") String username) {
        return userService.getFeedByUsername(username);
    }

    @DeleteMapping("/@{username}")
    public UserResponseDto deleteUserByUsername(@PathVariable("username") String username, @RequestBody CredentialDto credentials) {
        return userService.deleteUserByUsername(username, credentials);
    }
}

