package com.controller;

import com.entity.ConfigEntity;
import com.entity.EIException;
import com.service.ConfigService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FileControllerTest {

    @Mock
    private ConfigService configService;

    @InjectMocks
    private FileController controller;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
                "hello".getBytes(StandardCharsets.UTF_8));
        R result = controller.upload(file, null);
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("file"));
    }

    @Test
    public void testUploadWithFaceConfig() throws Exception {
        when(configService.selectOne(any())).thenReturn(null);
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg", "image/jpeg",
                new byte[]{1, 2, 3});
        R result = controller.upload(file, "1");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUploadWithTemplateType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "tpl.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1});
        R result = controller.upload(file, "import_template");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUploadEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        assertThrows(EIException.class, () -> controller.upload(file, null));
    }

    @Test
    public void testDownloadExistingFile() throws Exception {
        File uploadDir = tempDir.resolve("upload").toFile();
        uploadDir.mkdirs();
        File target = new File(uploadDir, "sample.txt");
        org.apache.commons.io.FileUtils.writeStringToFile(target, "download-me", StandardCharsets.UTF_8);

        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());
            ResponseEntity<byte[]> response = controller.download("sample.txt");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    public void testUploadWithExistingFaceConfig() throws Exception {
        ConfigEntity existing = new ConfigEntity();
        existing.setName("faceFile");
        existing.setValue("old.jpg");
        when(configService.selectOne(any())).thenReturn(existing);
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg", "image/jpeg",
                new byte[]{1, 2, 3});
        R result = controller.upload(file, "1");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDownloadMissingFile() {
        ResponseEntity<byte[]> response = controller.download("nonexistent-file-xyz.txt");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
