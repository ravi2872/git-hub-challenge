package com.github.challenge.service;

import java.util.List;
import java.util.Map;

import com.github.challenge.model.Follower;

public interface GitService {
	Map<String, List<Follower>> retrieveGitFollowersById(String gitId);
}
