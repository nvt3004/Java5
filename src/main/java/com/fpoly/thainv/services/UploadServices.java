package com.fpoly.thainv.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServices {
	public String save(MultipartFile file, String forder) {
		Path root = Paths.get("static/" + forder);

		if (!file.isEmpty()) {
			try {
				Files.createDirectories(root);
				String now = String.valueOf(new Date().getTime());

				String fileName = String.format("%s%s", now, ".jpg");
				Files.copy(file.getInputStream(), root.resolve(fileName));

				return fileName;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void delete(String fileName, String forder) {
		Path root = Paths.get("static/" + forder);
		Path filePath = root.resolve(fileName);
		
		try {
			if (Files.exists(filePath)) {
				Files.delete(filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
