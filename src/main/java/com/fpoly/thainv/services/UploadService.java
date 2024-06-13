package com.fpoly.thainv.services;

//PC06157 - Ngô Văn Thái

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
	public static String uploadFile(MultipartFile file) {
		if (!file.isEmpty()) {
			Path root = Paths.get("static/images");
			try {
				Files.createDirectories(root);
				String name = String.valueOf(new Date().getTime());
//				String fileName = file.get(index).getOriginalFilename();
				String fileName = String.format("%s%s", name, ".jpg");
				Files.copy(file.getInputStream(), root.resolve(fileName));
				return "/images/" + fileName;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				throw new RuntimeErrorException(null, "Could not initialize folder for upload");
			}
		}

		return null;
	}

	
}
