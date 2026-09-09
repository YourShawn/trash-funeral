package com.trashfuneral.funeral.service;

public interface VisionService {

    VisionResult identify(byte[] imageBytes, String contentType, String filename);
}
