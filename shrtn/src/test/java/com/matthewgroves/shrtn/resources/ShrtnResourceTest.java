package com.matthewgroves.shrtn.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.matthewgroves.shrtn.ShrtnConfiguration;
import com.matthewgroves.shrtn.api.ShortenedUrl;
import com.matthewgroves.shrtn.jdbi.UrlDAO;

public class ShrtnResourceTest {
	
	UrlDAO dao;
	ShrtnConfiguration config;
	ShrtnResource resource;
	
	@Before
	public void setupDao() {
		dao = mock(UrlDAO.class);
		
		config = new ShrtnConfiguration();
		config.setHostname("localhost");
		config.setRuntimePort(8080);
		
		resource = new ShrtnResource(dao, config);
	}
	
	@Test
	public void configurableUrlBase() {
		config.setHostname("www.shortener.com");
		config.setRuntimePort(12345);
		
		ShortenedUrl response = (ShortenedUrl) resource.addShortenedUrl("www.google.com");
		
		assertThat(response.getShortenedUrl()).contains(config.getHostname())
				.contains(Integer.toString(config.getRuntimePort()));
	}
	
	@Test
	public void hideDefaultPort() {
		config.setRuntimePort(80);
		
		ShortenedUrl response = (ShortenedUrl) resource.addShortenedUrl("www.google.com");
		
		assertThat(response.getShortenedUrl()).contains(config.getHostname())
				.doesNotContain(Integer.toString(config.getRuntimePort()));
	}
	
	@Test(expected = NotFoundException.class)
	public void redirectLinkNotFound() throws URISyntaxException {
		resource.redirectUrl("10");
	}
	
	@Test
	public void redirectLinkFound() throws URISyntaxException {
		final String EXAMPLE = "http://www.example.com";
		when(dao.findUrlById(anyLong())).thenReturn(EXAMPLE);
		
		Response res = resource.redirectUrl("10");
		
		assertThat(res.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
		
		assertThat(res.getLocation().toString()).isEqualTo(EXAMPLE);
	}
	
	@Test(expected = NotFoundException.class)
	public void apiLinkNotFound() {
		resource.getExpandedUrl("10");
	}
	
	@Test
	public void apiLinkFound() {
		final String EXAMPLE_URL = "http://www.example.com";
		when(dao.findUrlById(anyLong())).thenReturn(EXAMPLE_URL);
		
		final long EXAMPLE_ID = 1;
		ShortenedUrl result = resource.getExpandedUrl(Long.toString(EXAMPLE_ID));
		
		assertThat(result.getFullUrl()).isEqualTo(EXAMPLE_URL);
		
		assertThat(result.getId()).isEqualTo(EXAMPLE_ID);
	}
	
	@Test(expected = WebApplicationException.class)
	public void addNullUrl() {
		resource.addShortenedUrl(null);
	}
	
	@Test(expected = WebApplicationException.class)
	public void addZeroLengthUrl() {
		resource.addShortenedUrl("");
	}
	
	@Test(expected = WebApplicationException.class)
	public void addTooLongUrl() {
		final int BIG_STRING_SIZE = 3000;
		StringBuilder BIG_STRING = new StringBuilder(BIG_STRING_SIZE);
		for (int i = 0; i < BIG_STRING_SIZE; ++i)
			BIG_STRING.append('a');
		
		resource.addShortenedUrl(BIG_STRING.toString());
	}
	
	// TODO - need to add detection for this
	//
	// @Test
	// public void addMalformedUrl() {
	// // when
	// Throwable thrown = Assertions.catchThrowable(() -> {
	// resource.addShortenedUrl("bar");
	// });
	//
	// // then
	// assertThat(thrown).isInstanceOf(WebApplicationException.class);
	// }
	
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
