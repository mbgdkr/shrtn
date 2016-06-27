package matthewgroves.urlShortener.api;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

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