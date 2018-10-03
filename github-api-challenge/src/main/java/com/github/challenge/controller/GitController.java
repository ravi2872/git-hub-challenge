package com.github.challenge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.challenge.model.Follower;
import com.github.challenge.service.GitService;

@RestController
@RequestMapping("")
public class GitController {

	@Autowired
	public GitService gitService;

	@GetMapping(path = "/{gitId}/getFollowers", produces = "application/json")
	public ResponseEntity<Map<String, List<Follower>>> getGitFollowers(@PathVariable String gitId) {
		Map<String, List<Follower>> followerList = gitService.retrieveGitFollowersById(gitId);
		return ResponseEntity.ok(followerList);
	}
	
	@GetMapping(path = "/{gitId}/getRepos", produces = "application/json")
	public ResponseEntity<Map<String, List<String>>> getGitRepos(@PathVariable String gitId) {
		Map<String, List<String>> followerList = gitService.retrieveGitReposById(gitId);
		return ResponseEntity.ok(followerList);
	}
}
