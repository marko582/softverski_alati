package com.gymcore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gymcore")
public class GymcoreProperties {

	private Jwt jwt = new Jwt();
	private Videos videos = new Videos();
	private Cookie cookie = new Cookie();
	private Ollama ollama = new Ollama();

	public Jwt getJwt() {
		return jwt;
	}

	public void setJwt(Jwt jwt) {
		this.jwt = jwt;
	}

	public Videos getVideos() {
		return videos;
	}

	public void setVideos(Videos videos) {
		this.videos = videos;
	}

	public Cookie getCookie() {
		return cookie;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	public Ollama getOllama() {
		return ollama;
	}

	public void setOllama(Ollama ollama) {
		this.ollama = ollama;
	}

	public static class Jwt {
		private String secret;
		private long accessExpirationMs = 86_400_000L;
		private long refreshExpirationMs = 604_800_000L;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public long getAccessExpirationMs() {
			return accessExpirationMs;
		}

		public void setAccessExpirationMs(long accessExpirationMs) {
			this.accessExpirationMs = accessExpirationMs;
		}

		public long getRefreshExpirationMs() {
			return refreshExpirationMs;
		}

		public void setRefreshExpirationMs(long refreshExpirationMs) {
			this.refreshExpirationMs = refreshExpirationMs;
		}
	}

	/** Local exercise demo videos (same files as legacy fitness-api/videos). */
	public static class Videos {
		private String directory = "../fitness-api/videos";

		public String getDirectory() {
			return directory;
		}

		public void setDirectory(String directory) {
			this.directory = directory;
		}
	}

	public static class Cookie {
		private String refreshName = "refreshToken";
		private boolean secure;

		public String getRefreshName() {
			return refreshName;
		}

		public void setRefreshName(String refreshName) {
			this.refreshName = refreshName;
		}

		public boolean isSecure() {
			return secure;
		}

		public void setSecure(boolean secure) {
			this.secure = secure;
		}
	}

	/** Local Ollama instance — no cloud API key required. */
	public static class Ollama {
		private boolean enabled = true;
		private String baseUrl = "http://localhost:11434";
		/** Model name as shown by {@code ollama list}, e.g. {@code llama3.2:3b}. */
		private String model = "llama3.2:3b";
		/** Max wait for generation (local models can be slow on CPU). */
		private long readTimeoutMs = 300_000L;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public long getReadTimeoutMs() {
			return readTimeoutMs;
		}

		public void setReadTimeoutMs(long readTimeoutMs) {
			this.readTimeoutMs = readTimeoutMs;
		}
	}
}
