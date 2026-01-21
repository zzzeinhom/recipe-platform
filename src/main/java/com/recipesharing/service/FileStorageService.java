package com.recipesharing.service;

public interface FileStorageService {
    /**
     * Store bytes at the given relative path and filename.
     * Returns the public URL that clients can use.
     */
    String store(byte[] data, String dirRelativePath, String filename);

    /**
     * Delete stored resource by its relative path (or URL).
     */
    void delete(String publicUrlOrPath);
}