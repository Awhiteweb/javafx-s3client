package com.whiteslife.aws.s3;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


public class S3ObjectList {
    private String bucket;
    private Stream<String> keys;
    private String startAfter;
    private boolean truncated;

    public S3ObjectList() {}

    public S3ObjectList(String bucket, Stream<String> keys, String startAfter, boolean truncated ) {
        this.setBucket( bucket );
        this.setKeys( keys );
        this.setStartAfter( startAfter );
        this.setTruncated( truncated );
    }

    public Stream<String> getKeys() {
        return keys;
    }

    public void setKeys( Stream<String> keys ) {
        this.keys = keys;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated( boolean truncated ) {
        this.truncated = truncated;
    }

    public String getStartAfter() {
        return startAfter;
    }

    public void setStartAfter( String startAfter ) {
        this.startAfter = startAfter;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket( String bucket ) {
        this.bucket = bucket;
    }
}
