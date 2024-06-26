package com.cooksys.group_project_1_team_1.services.impl;

import com.cooksys.group_project_1_team_1.entities.Tweet;
import com.cooksys.group_project_1_team_1.entities.User;
import com.cooksys.group_project_1_team_1.exceptions.BadRequestException;
import com.cooksys.group_project_1_team_1.exceptions.NotFoundException;
import com.cooksys.group_project_1_team_1.mappers.CredentialsMapper;
import com.cooksys.group_project_1_team_1.mappers.TweetMapper;
import com.cooksys.group_project_1_team_1.mappers.UserMapper;
import com.cooksys.group_project_1_team_1.models.CredentialDto;
import com.cooksys.group_project_1_team_1.models.TweetResponseDto;
import com.cooksys.group_project_1_team_1.models.UserResponseDto;
import com.cooksys.group_project_1_team_1.repositories.TweetRepository;
import com.cooksys.group_project_1_team_1.repositories.UserRepository;
import com.cooksys.group_project_1_team_1.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialsMapper credentialsMapper;
    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;

    @Override
    public List<TweetResponseDto> getFeedByUsername(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        List<Tweet> tweets = tweetRepository.findAllByAuthorIdAndDeletedFalse(user.getId());
        return tweetMapper.entitiesToResponseDtos(tweets);
    }

    @Override
    public UserResponseDto deleteUserByUsername(String username, CredentialDto credentials) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }
        if (!user.getCredentials().equals(credentialsMapper.requestDtoToEntity(credentials))) {
            throw new BadRequestException("Credentials for user you are trying to delete do not match.");
        }

        user.setDeleted(true);
        for (Tweet tweet : user.getTweets()) {
            tweet.setDeleted(true);
            tweetRepository.saveAndFlush(tweet);
        }
        userRepository.saveAndFlush(user);

        return userMapper.entityToResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        return userMapper.entityToResponseDto(user);
    }

    @Override
    public List<TweetResponseDto> getTweetsByUsername(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }
        user.getTweets().sort(Comparator.comparing(Tweet::getPosted).reversed());

        return tweetMapper.entitiesToResponseDtos(user.getTweets());
    }

    @Override
    public List<TweetResponseDto> getUserMentionsByUsername(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        List<Tweet> mentionedTweets = new ArrayList<>();

        for (Tweet tweet : tweetRepository.findAll()) {
            if (tweet.getContent() != null && tweet.getContent().contains("@" + username)) {
                mentionedTweets.add(tweet);
                System.out.println("Found one");
            }
        }

        mentionedTweets.sort(Comparator.comparing(Tweet::getPosted).reversed());

        return tweetMapper.entitiesToResponseDtos(mentionedTweets);
    }

    @Override
    public List<UserResponseDto> getUserFollowingByUsername(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        List<User> following = new ArrayList<>();

        for (User u : user.getFollowing()) {
            if (!u.isDeleted()) {
                following.add(u);
            }
        }

        return userMapper.entitiesToResponseDtos(following);
    }

    @Override
    public List<UserResponseDto> getUserFollowsByUsername(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        List<User> follows = new ArrayList<>();

        for (User u : user.getFollowers()) {
            if (!u.isDeleted()) {
                follows.add(u);
            }
        }

        return userMapper.entitiesToResponseDtos(follows);
    }
}
