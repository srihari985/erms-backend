//package com.erms.ERMS_Application.Demo.video;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/api/videos")
//public class DemoVideoController {
//    @Autowired
//    private DemoVideoService demoVideoService;
//
//    @PostMapping("/save/{sId}")
//    public DemoVideo uploadVideo(@PathVariable long sId, @RequestParam String title, @RequestParam("file") MultipartFile file) throws IOException {
//        // Save uploaded video temporarily
//        File tempFile = File.createTempFile("upload", ".mp4");
//        file.transferTo(tempFile);
//
//        // Compress video before saving to the database
//        byte[] compressedVideoData = demoVideoService.compressAndSaveVideo(tempFile, title);
//
//        // Clean up temporary file
//        tempFile.delete();
//
//        return demoVideoService.saveVideo(sId, title, compressedVideoData);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<byte[]> getVideo(@PathVariable Long id) {
//        byte[] videoData = demoVideoService.getVideo(id);
//        return videoData != null ? ResponseEntity.ok(videoData) : ResponseEntity.notFound().build();
//    }
//}
