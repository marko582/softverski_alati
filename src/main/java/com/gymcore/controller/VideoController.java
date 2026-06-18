package com.gymcore.controller;

import com.gymcore.config.GymcoreProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VideoController {

	private final Path videosDir;

	public VideoController(GymcoreProperties properties) {
		this.videosDir = Path.of(properties.getVideos().getDirectory()).toAbsolutePath().normalize();
	}

	@GetMapping("/videos/{*filepath}")
	public ResponseEntity<?> streamVideo(@PathVariable("filepath") String filepath, HttpServletRequest request)
			throws IOException {
		if (!isSafeRelativeVideoPath(filepath)) {
			return ResponseEntity.notFound().build();
		}
		Path file = videosDir.resolve(filepath.replace('\\', '/')).normalize();
		if (!file.startsWith(videosDir) || !Files.isRegularFile(file)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new FileSystemResource(file);
		long contentLength = resource.contentLength();
		String contentType = Files.probeContentType(file);
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		String rangeHeader = request.getHeader(HttpHeaders.RANGE);
		if (rangeHeader == null || rangeHeader.isBlank()) {
			return ResponseEntity.ok()
					.header(HttpHeaders.ACCEPT_RANGES, "bytes")
					.contentType(MediaType.parseMediaType(contentType))
					.contentLength(contentLength)
					.body(resource);
		}

		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		if (ranges.isEmpty()) {
			return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
					.header(HttpHeaders.ACCEPT_RANGES, "bytes")
					.build();
		}

		HttpRange range = ranges.get(0);
		long start = range.getRangeStart(contentLength);
		long end = range.getRangeEnd(contentLength);
		long rangeLength = end - start + 1;

		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
				.header(HttpHeaders.ACCEPT_RANGES, "bytes")
				.header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + contentLength)
				.contentType(MediaType.parseMediaType(contentType))
				.contentLength(rangeLength)
				.body(new ResourceRegion(resource, start, rangeLength));
	}

	private static boolean isSafeRelativeVideoPath(String filepath) {
		if (filepath == null || filepath.isBlank()) {
			return false;
		}
		if (filepath.contains("..")) {
			return false;
		}
		for (String seg : filepath.split("/")) {
			if (".".equals(seg) || "..".equals(seg)) {
				return false;
			}
			if (seg.contains("\\")) {
				return false;
			}
		}
		return true;
	}
}
