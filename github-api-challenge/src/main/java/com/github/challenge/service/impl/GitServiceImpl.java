package com.github.challenge.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.github.challenge.model.Follower;
import com.github.challenge.model.Repository;
import com.github.challenge.service.GitService;

@Service
@Transactional
public class GitServiceImpl implements GitService {

	private String GIT_FOLLOWER_URL = "https://api.github.com/users/{0}/followers";

	private String GIT_REPO_URL = "https://api.github.com/users/{0}/repos";

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
			if (i == 3)
				break;
			List<Follower> followers_2 = getFollowers(follower.getFollowers_url());
			map.put(follower.getLogin(), getSubList(followers_2, 5));
			if (CollectionUtils.isNotEmpty(followers_2)) {
				int j = 0;
				for (Follower follower2 : followers_2) {
					if (j == 3)
						break;
					List<Follower> followers_3 = getFollowers(follower2.getFollowers_url());
					map.put(follower2.getLogin(), getSubList(followers_3, 5));
					if (CollectionUtils.isNotEmpty(followers_3)) {
						int k = 0;
						for (Follower follower3 : followers_3) {
							if (k == 3)
								break;
							List<Follower> followers = getFollowers(follower3.getFollowers_url());
							map.put(follower3.getLogin(), getSubList(followers, 5));
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

	@Override
	public Map<String, List<String>> retrieveGitReposById(String gitId) {
		String _url = getUrl(gitId);
		Map<String, List<String>> map = new ConcurrentHashMap<>();

		List<Follower> repositories = getFollowers(_url);

		if (CollectionUtils.isEmpty(repositories)) {
			map.put(gitId, new ArrayList<>());
			return map;
		}

		int i = 0;
		for (Follower follower : repositories) {
			if (i == 3)
				break;
			List<Repository> repository_2 = getRepositories(follower.getRepos_url());
			map.put(follower.getLogin(), getSubListForRepos(repository_2, 3).stream().map(Repository::getFollower)
					.map(Follower::getRepos_url).collect(Collectors.toList()));
			if (CollectionUtils.isNotEmpty(repository_2)) {
				int j = 0;
				for (Repository repo2 : repository_2) {
					if (j == 3)
						break;
					List<Repository> repository_3 = getRepositories(repo2.getFollower().getRepos_url());
					map.put(repo2.getFollower().getLogin(), getSubListForRepos(repository_3, 3).stream()
							.map(Repository::getFollower).map(Follower::getRepos_url).collect(Collectors.toList()));
					if (CollectionUtils.isNotEmpty(repository_3)) {
						int k = 0;
						for (Repository repo3 : repository_3) {
							if (k == 3)
								break;
							List<Repository> repos = getRepositories(repo3.getFollower().getRepos_url());
							map.put(repo3.getFollower().getLogin(),
									getSubListForRepos(repos, 3).stream().map(Repository::getFollower)
											.map(Follower::getRepos_url).collect(Collectors.toList()));
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

	public List<Follower> getSubList(List<Follower> fullList, int limit) {
		if (CollectionUtils.isEmpty(fullList)) {
			return new ArrayList<>();
		}

		if (fullList.size() > 5) {
			return fullList.subList(0, 5);
		} else {
			return fullList;
		}
	}

	public List<Repository> getSubListForRepos(List<Repository> fullList, int limit) {
		if (CollectionUtils.isEmpty(fullList)) {
			return new ArrayList<>();
		}

		if (fullList.size() > 5) {
			return fullList.subList(0, 5);
		} else {
			return fullList;
		}
	}

	private String getUrl(String gitId) {
		return MessageFormat.format(GIT_FOLLOWER_URL, gitId);
	}

	private List<Follower> getFollowers(String _url) {
		ResponseEntity<Follower[]> response = restTemplate.getForEntity(_url, Follower[].class);
		List<Follower> followerList = Arrays.asList(response.getBody());
		return followerList;
	}

	private List<Repository> getRepositories(String _url) {
		ResponseEntity<Repository[]> response = restTemplate.getForEntity(_url, Repository[].class);
		List<Repository> repositories = Arrays.asList(response.getBody());
		return repositories;
	}

}
