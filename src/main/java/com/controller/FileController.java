package com.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.entity.EIException;
import com.service.ConfigService;
import com.utils.R;

/**
 * 上传文件映射表
 */
@RestController
@RequestMapping("file")
@SuppressWarnings({"unchecked","rawtypes"})
public class FileController{
	@Autowired
    private ConfigService configService;

	private static final Pattern SAFE_NAME = Pattern.compile("^[a-zA-Z0-9._-]+$");

	private File uploadDir() throws IOException {
		File path = new File(ResourceUtils.getURL("classpath:static").getPath());
		if (!path.exists()) {
			path = new File("");
		}
		File upload = new File(path.getAbsolutePath(), "upload");
		if (!upload.exists()) {
			upload.mkdirs();
		}
		return upload;
	}

	private File resolveUnderUpload(File upload, String fileName) throws IOException {
		if (StringUtils.isBlank(fileName) || !SAFE_NAME.matcher(fileName).matches()) {
			throw new EIException("非法文件名");
		}
		Path base = upload.getCanonicalFile().toPath().normalize();
		Path resolved = base.resolve(fileName).normalize();
		if (!resolved.startsWith(base)) {
			throw new EIException("非法文件路径");
		}
		return resolved.toFile();
	}

	/**
	 * 上传文件
	 */
	@RequestMapping("/upload")
    @IgnoreAuth
	public R upload(@RequestParam("file") MultipartFile file, String type) throws Exception {
		if (file == null || file.isEmpty()) {
			throw new EIException("上传文件不能为空");
		}
		String original = file.getOriginalFilename();
		if (StringUtils.isBlank(original) || !original.contains(".")) {
			throw new EIException("文件名无效");
		}
		String fileExt = original.substring(original.lastIndexOf('.') + 1);
		if (!SAFE_NAME.matcher(fileExt).matches()) {
			throw new EIException("非法扩展名");
		}
		File upload = uploadDir();
		String fileName = new Date().getTime() + "." + fileExt;
        if (StringUtils.isNotBlank(type) && type.contains("_template")) {
			if (!SAFE_NAME.matcher(type).matches()) {
				throw new EIException("非法模板类型");
			}
            fileName = type + "." + fileExt;
        }
		File dest = resolveUnderUpload(upload, fileName);
		file.transferTo(dest);
		if (StringUtils.isNotBlank(type) && type.equals("1")) {
			ConfigEntity configEntity = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
			if (configEntity == null) {
				configEntity = new ConfigEntity();
				configEntity.setName("faceFile");
				configEntity.setValue(fileName);
			} else {
				configEntity.setValue(fileName);
			}
			configService.insertOrUpdate(configEntity);
		}
		return R.ok().put("file", fileName);
	}

	/**
	 * 下载文件
	 */
	@IgnoreAuth
	@RequestMapping("/download")
	public ResponseEntity<byte[]> download(@RequestParam String fileName) {
		try {
			File upload = uploadDir();
			File file = resolveUnderUpload(upload, fileName);
			if (file.exists() && file.isFile()) {
				HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			    headers.setContentDispositionFormData("attachment", file.getName());
			    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
			}
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		} catch (EIException e) {
			return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
