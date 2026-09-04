package com.semmelzahntiger.brainrotbackend.service.data;

import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class FileDecoderService {


    private final long maxEntryMegabytes;
    private final long maxTotalMegabytes;
    private final long maxEntrySize;
    private final long maxTotalSize;
    private final int MAX_ENTRIES = 300;
    private final int BUFFER_SIZE = 8192;

    public FileDecoderService(@Value("${spring.servlet.multipart.max-file-size}") String fileSize) {
        this.maxTotalMegabytes = DataSize.parse(fileSize).toMegabytes();
        this.maxTotalSize = maxTotalMegabytes * 1024 * 1024;
        this.maxEntryMegabytes = maxTotalMegabytes;
        this.maxEntrySize = maxTotalSize;
    }

    /**
     * Decodes a zip file
     * @param inputStream InputStream of zip file
     * @return Map of all entries and their data
     * @throws IOException
     */
    public Map<String, byte[]> extract(InputStream inputStream) throws IOException, SecurityException {
        Map<String, byte[]> result = new HashMap<>();
        long totalUncompressedSize = 0L;
        int entryCount = 0;

        // Open zip file
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            // Check next entry. Entry can be directory as well as file, close entry when it's a dir
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                entryCount++;
                if(entryCount > MAX_ENTRIES) {
                    throw new FileCountLimitExceededException("Too many entries", MAX_ENTRIES);
                }
                if(zipEntry.isDirectory()) {
                    zipInputStream.closeEntry();
                    continue;
                }
                // Check entry name for malicious structure
                String clearedName = getCleanEntryName(zipEntry.getName());

                // Ignore non JSON files
                if(!clearedName.endsWith(".json")) {
                    zipInputStream.closeEntry();
                    continue;
                }
                // Output Stream for current Entry
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                long entryTotal = 0;
                int length;

                // zipInputStream reading returns -1 if entry is finished, not only when the entire thing is finished
                while ((length = zipInputStream.read(buffer)) != -1) {
                    entryTotal += length;
                    totalUncompressedSize += length;

                    if(entryTotal > maxEntrySize) {
                        throw new FileSizeLimitExceededException("Individual Zip Entry too large. (Max " + maxEntryMegabytes + " MB)", entryTotal, maxEntrySize);
                    }
                    if(totalUncompressedSize > maxTotalSize) {
                        throw new FileSizeLimitExceededException("Zip file too large. (Max: " + maxTotalMegabytes + " MB)", totalUncompressedSize, maxTotalSize);
                    }
                    outputStream.write(buffer, 0, length);
                }
                // Write whole entries data into map
                result.put(clearedName, outputStream.toByteArray());
                zipInputStream.closeEntry();
            }
        }
        return result;
    }


    private String getCleanEntryName(String entryName) {
        String normalizedEntryName = Paths.get(entryName).normalize().toString().replace('\\', '/');
        if(normalizedEntryName.startsWith("../") || normalizedEntryName.contains("/../") || normalizedEntryName.equals("..")) {
            throw new SecurityException("Zip Entry contains malicious structure" + entryName);
        }
        return normalizedEntryName;
    }
}
