package com.matthewgroves.shrtn.api;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO representing a URL record in the DB
 * 
 * @author Matthew Groves <matthew.b.groves@gmail.com>
 *
 */
public class ShortenedUrl {
	private long id;
	
	private String fullUrl;
	
	@Length(max = 39)
	private String shortenedUrl;
	
	public ShortenedUrl() {
		// Jackson deserialization
	}
	
	public ShortenedUrl(long id, String fullUrl, String shortenedUrl) {
		this.id = id;
		this.fullUrl = fullUrl;
		this.shortenedUrl = shortenedUrl;
	}
	
	@JsonProperty
	public long getId() {
		return id;
	}
	
	@JsonProperty
	public String getFullUrl() {
		return fullUrl;
	}
	
	@JsonProperty
	public String getShortenedUrl() {
		return shortenedUrl;
	}
}