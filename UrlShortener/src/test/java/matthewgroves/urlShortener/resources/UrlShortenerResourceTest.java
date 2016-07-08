package matthewgroves.urlShortener.resources;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import matthewgroves.urlShortener.HttpConnectionFactory;
import matthewgroves.urlShortener.api.ShortenedUrl;
import matthewgroves.urlShortener.db.UrlDAO;

public class UrlShortenerResourceTest {

	UrlDAO dao;
	HttpConnectionFactory httpConnection;;
	UrlShortenerResource resource;
	
	@Before
	public void setupDao() {
		dao = mock(UrlDAO.class);
		
		httpConnection = new HttpConnectionFactory();
		httpConnection.setHost("localhost");
		httpConnection.setPort(8080);
		
		resource = new UrlShortenerResource(dao, httpConnection);
	}

	@Test
	public void configurableUrlBase() {
		httpConnection.setHost("www.shortener.com");
		httpConnection.setPort(12345);
		
		ShortenedUrl response = (ShortenedUrl) resource.addShortenedUrl("www.google.com").getEntity();
		
		assertThat(response.getShortenedUrl())
			.contains(httpConnection.getHost())
			.contains(Integer.toString(httpConnection.getPort()));
	}
	
	@Test
	public void linkNotFound() {
		assertThat(resource.expandUrl("10").getStatus())
			.isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void linkFound() {
		final String EXAMPLE = "http://www.example.com";
		when(dao.findUrlById(anyLong())).thenReturn(EXAMPLE);
		
		Response res = resource.expandUrl("10");
		
		assertThat(res.getStatus())
			.isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
		
		assertThat(res.getLocation().toString())
			.isEqualTo(EXAMPLE);
	}

}
