package com.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileUtil unit tests.
 */
public class FileUtilTest {

    private File tempFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testFileToByte_readsContent() throws IOException {
        tempFile = File.createTempFile("file-util-", ".bin");
        byte[] expected = "warehouse-file-content".getBytes(StandardCharsets.UTF_8);
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(expected);
        }

        byte[] actual = FileUtil.FileToByte(tempFile);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testFileToByte_emptyFile() throws IOException {
        tempFile = Files.createTempFile("file-util-empty-", ".txt").toFile();
        byte[] actual = FileUtil.FileToByte(tempFile);
        assertNotNull(actual);
        assertEquals(0, actual.length);
    }
}
