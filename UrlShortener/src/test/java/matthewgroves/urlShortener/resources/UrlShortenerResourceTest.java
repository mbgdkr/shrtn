package matthewgroves.urlShortener.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import matthewgroves.urlShortener.UrlShortenerConfiguration;
import matthewgroves.urlShortener.api.ShortenedUrl;
import matthewgroves.urlShortener.db.UrlDAO;

public class UrlShortenerResourceTest {
	
	UrlDAO dao;
	UrlShortenerConfiguration config;
	UrlShortenerResource resource;
	
	@Before
	public void setupDao() throws Exception {
		dao = mock(UrlDAO.class);
		
		config = new UrlShortenerConfiguration();
		config.setHostname("localhost");
		config.configurePort(8080);
		
		resource = new UrlShortenerResource(dao, config);
	}
	
	@Test
	public void configurableUrlBase() throws Exception {
		config.setHostname("www.shortener.com");
		config.configurePort(12345);
		
		ShortenedUrl response = (ShortenedUrl) resource.addShortenedUrl("www.google.com");
		
		assertThat(response.getShortenedUrl()).contains(config.getHostname())
				.contains(Integer.toString(config.getRuntimePort()));
	}
	
	@Test
	public void redirectLinkNotFound() throws URISyntaxException {
		// when
		Throwable thrown = Assertions.catchThrowable(() -> {
			resource.redirectUrl("10");
		});
		
		// then
		assertThat(thrown).isInstanceOf(NotFoundException.class);
	}
	
	@Test
	public void redirectLinkFound() throws URISyntaxException {
		final String EXAMPLE = "http://www.example.com";
		when(dao.findUrlById(anyLong())).thenReturn(EXAMPLE);
		
		Response res = resource.redirectUrl("10");
		
		assertThat(res.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
		
		assertThat(res.getLocation().toString()).isEqualTo(EXAMPLE);
	}
	
	@Test
	public void apiLinkNotFound() throws URISyntaxException {
		// when
		Throwable thrown = Assertions.catchThrowable(() -> {
			resource.getExpandedUrl("10");
		});
		
		// then
		assertThat(thrown).isInstanceOf(NotFoundException.class);
	}
	
	@Test
	public void apiLinkFound() throws URISyntaxException {
		final String EXAMPLE_URL = "http://www.example.com";
		when(dao.findUrlById(anyLong())).thenReturn(EXAMPLE_URL);
		
		final long EXAMPLE_ID = 1;
		ShortenedUrl result = resource.getExpandedUrl(Long.toString(EXAMPLE_ID));
		
		assertThat(result.getFullUrl()).isEqualTo(EXAMPLE_URL);
		
		assertThat(result.getId()).isEqualTo(EXAMPLE_ID);
	}
	
	@Test
	public void addNullUrl() {
		// when
		Throwable thrown = Assertions.catchThrowable(() -> {
			resource.addShortenedUrl(null);
		});
		
		// then
		assertThat(thrown).isInstanceOf(WebApplicationException.class);
	}
	
	@Test
	public void addZeroLengthUrl() {
		// when
		Throwable thrown = Assertions.catchThrowable(() -> {
			resource.addShortenedUrl("");
		});
		
		// then
		assertThat(thrown).isInstanceOf(WebApplicationException.class);
	}
	
	@Test
	public void addTooLongUrl() {
		final int BIG_STRING_SIZE = 3000;
		StringBuilder BIG_STRING = new StringBuilder(BIG_STRING_SIZE);
		for(int i = 0; i < BIG_STRING_SIZE; ++i)
			BIG_STRING.append('a');
		
		// when
		Throwable thrown = Assertions.catchThrowable(() -> {
			resource.addShortenedUrl(BIG_STRING.toString());
		});
		
		// then
		assertThat(thrown).isInstanceOf(WebApplicationException.class);
	}
	
//TODO - need to add detection for this
//	
//	@Test
//	public void addMalformedUrl() {
//		// when
//		Throwable thrown = Assertions.catchThrowable(() -> {
//			resource.addShortenedUrl("bar");
//		});
//		
//		// then
//		assertThat(thrown).isInstanceOf(WebApplicationException.class);
//	}
	
	@Test
	public void addValidUrl() {
		final String EXAMPLE_URL = "http://www.google.com";
		final long EXAMPLE_ID = 1;
		when(dao.insertUrl(EXAMPLE_URL)).thenReturn(EXAMPLE_ID);
		
		ShortenedUrl sUrl = resource.addShortenedUrl(EXAMPLE_URL);
		
		assertThat(sUrl.getFullUrl()).isEqualTo(EXAMPLE_URL);
		
		assertThat(sUrl.getId()).isEqualTo(EXAMPLE_ID);
	}
}