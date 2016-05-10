package com.github.usethevoid.mapsites.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import org.apache.commons.io.FilenameUtils;


public class FileStorage {
	final String uploadPath;
	final String urlPath;
	
	public FileStorage(String uploadPath, String urlPath) throws IOException {
		this.uploadPath = uploadPath;
		this.urlPath = urlPath;
		java.nio.file.Path dirPath = FileSystems.getDefault().getPath(uploadPath);
		Files.createDirectories(dirPath);
	}
	
	private static String toPrettyName(String string) {
	    return Normalizer.normalize(string.toLowerCase(), Form.NFD)
	        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	        .replaceAll("[^\\p{Alnum}]+", "-");
	}
	
	public String put(InputStream fileInputStream, String fileName, String key) {
		String basename = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		String savedFileName = toPrettyName(basename)+"."+extension;
		java.nio.file.Path dirPath = FileSystems.getDefault().getPath(uploadPath, key);
		java.nio.file.Path outputPath = FileSystems.getDefault().getPath(uploadPath, key, savedFileName);
		try {
			Files.createDirectories(dirPath);
			Files.copy(fileInputStream, outputPath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return savedFileName;
	}
	
	public String getPath() {
		return urlPath;
	}
}
