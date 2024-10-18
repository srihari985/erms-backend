//package com.erms.ERMS_Application.Demo.video;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//
//@Service
//public class DemoVideoService {
//
//    @Autowired
//    private DemoVideoRepository demoVideoRepository;
//
//    public DemoVideo saveVideo(long sId, String title, byte[] videoData) {
//        DemoVideo video = new DemoVideo();
//        video.setTitle(title);
//        video.setVideoData(videoData);
//        return demoVideoRepository.save(video);
//    }
//
//    public byte[] getVideo(Long id) {
//        DemoVideo video = demoVideoRepository.findById(id).orElse(null);
//        return video != null ? video.getVideoData() : null;
//    }
//
//    public byte[] compressAndSaveVideo(File inputFile, String title) throws IOException {
//        // Set output file
//        File outputFile = File.createTempFile("compressed_", ".mp4");
//
//        // FFmpeg command to compress the video
//        String command = String.format("ffmpeg -i %s -vf scale=480:360 -b:v 500k -b:a 128k -y %s",
//                inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
//
//        // Execute FFmpeg command
//        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
//        processBuilder.redirectErrorStream(true); // Merge error and output stream
//        Process process = processBuilder.start();
//
//        // Wait for the compression process to complete
//        try {
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                throw new IOException("FFmpeg compression failed with exit code " + exitCode);
//            }
//        } catch (InterruptedException e) {
//            throw new IOException("Compression process was interrupted", e);
//        }
//
//        // Read compressed video data
//        byte[] compressedVideoData = Files.readAllBytes(outputFile.toPath());
//
//        // Clean up the output file
//        outputFile.delete();
//
//        return compressedVideoData;
//    }
//}
