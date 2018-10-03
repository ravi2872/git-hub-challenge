package com.github.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
	@JsonProperty("id")
	private String id;
	@JsonProperty("owner")
	private Follower follower;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Follower getFollower() {
		return follower;
	}

	public void setFollower(Follower follower) {
		this.follower = follower;
	}
}
