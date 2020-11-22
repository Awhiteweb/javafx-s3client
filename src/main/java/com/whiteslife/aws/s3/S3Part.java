package com.whiteslife.aws.s3;

public class S3Part {
    private String partETag;
    private long size;

    public S3Part(String partETag, long size) {
        this.partETag = partETag;
        this.size = size;
    }

    public String getPartETag() {
        return partETag;
    }

    public long getSize() {
        return size;
    }
}
