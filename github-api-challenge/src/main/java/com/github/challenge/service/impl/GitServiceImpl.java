package com.github.challenge.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.challenge.model.Follower;
import com.github.challenge.model.FollowerList;
import com.github.challenge.service.GitService;

@Service
@Transactional
public class GitServiceImpl implements GitService {

	private String GIT_FOLLOWER_URL = "https://api.github.com/users/{0}/followers";

	@Autowired
	RestTemplate restTemplate;

	@Override
	public Map<String, List<Follower>> retrieveGitFollowersById(String gitId) {
		String _url = getUrl(gitId);
		Map<String, List<Follower>> map = new ConcurrentHashMap<>();

		List<Follower> followerList = getFollowers(_url);

		if (CollectionUtils.isEmpty(followerList)) {
			map.put(gitId, followerList);
			return map;
		}

		int i = 0;
		for (Follower follower : followerList) {
			if (i == 4)
				break;
			addToMap(follower, map);
			List<Follower> followers_2 = getFollowers(getUrl(follower.getLogin()));
			if (CollectionUtils.isNotEmpty(followers_2)) {
				int j = 0;
				for (Follower follower2 : followers_2) {
					if (j == 4)
						break;
					addToMap(follower2, map);
					List<Follower> followers_3 = getFollowers(getUrl(follower2.getLogin()));
					if (CollectionUtils.isNotEmpty(followers_3)) {
						int k = 0;
						for (Follower follower3 : followers_3) {
							if (k == 4)
								break;
							addToMap(follower3, map);
							List<Follower> followers = getFollowers(getUrl(follower3.getLogin()));
							if (CollectionUtils.isNotEmpty(followers)) {
								if (followers.size() > 5) {
									map.put(follower3.getLogin(), followers.subList(0, 4));
								} else {
									map.put(follower3.getLogin(), followers);
								}

							}
							k++;
						}
					}
					j++;
				}
			}
			i++;
		}
		return map;
	}

	private void addToMap(Follower follower, Map<String, List<Follower>> map) {
		List<Follower> list = map.get(follower.getLogin());
		if (CollectionUtils.isEmpty(list)) {
			map.put(follower.getLogin(), new ArrayList<>());
			return;
		}
		System.out.println("Before "+follower.getLogin() + ":"+map.get(follower.getLogin()));
		list.add(follower);
		System.out.println("After "+follower.getLogin() + ":"+map.get(follower.getLogin()));
	}

	private String getUrl(String gitId) {
		return MessageFormat.format(GIT_FOLLOWER_URL, gitId);
	}

	private List<Follower> getFollowers(String _url) {
		ResponseEntity<Follower[]> response = restTemplate.getForEntity(_url, Follower[].class);
		List<Follower> followerList = Arrays.asList(response.getBody());
		return followerList;
	}

}
