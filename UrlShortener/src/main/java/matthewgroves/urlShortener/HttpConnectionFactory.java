package matthewgroves.urlShortener;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class for HTTP server
 * 
 * @author Matthew Groves <matthew.b.groves@gmail.com>
 *
 */
public class HttpConnectionFactory {
	@NotEmpty
	private String host;
	
	@Min(1)
	@Max(65535)
	private int port = 8080;
	
	@JsonProperty
	public String getHost() {
		return host;
	}
	
	@JsonProperty
	public void setHost(String host) {
		this.host = host;
	}
	
	@JsonProperty
	public int getPort() {
		return port;
	}
	
	@JsonProperty
	public void setPort(int port) {
		this.port = port;
	}
}