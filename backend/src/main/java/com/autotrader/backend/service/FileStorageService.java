package com.autotrader.backend.service;

import java.io.InputStream;

public interface FileStorageService {

    void saveFile(InputStream inputStream, String storageFilename);

    void deleteFile(String storageFilename);

    String getFileUrl(String storageFilename);
}