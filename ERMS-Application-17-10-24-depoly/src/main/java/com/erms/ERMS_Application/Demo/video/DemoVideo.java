//package com.erms.ERMS_Application.Demo.video;
//
//import jakarta.persistence.*;
//
//@Entity
//public class DemoVideo {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    @Lob // Indicates that the field should be treated as a BLOB
//    private byte[] videoData;
//
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public byte[] getVideoData() {
//        return videoData;
//    }
//
//    public void setVideoData(byte[] videoData) {
//        this.videoData = videoData;
//    }
//
//    public DemoVideo() {
//    }
//
//    public DemoVideo(Long id, String title, byte[] videoData) {
//        this.id = id;
//        this.title = title;
//        this.videoData = videoData;
//    }
//}
