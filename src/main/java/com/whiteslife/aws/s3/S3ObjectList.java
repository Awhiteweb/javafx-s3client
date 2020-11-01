package com.whiteslife.aws.s3;

import java.util.LinkedList;
import java.util.List;


public class S3ObjectList {
    private List<String> keys;
    private boolean truncated;

    public S3ObjectList() {
        this.keys = new LinkedList<>();
    }

    public S3ObjectList(List<String> keys, boolean truncated ) {
        this.addKeys( keys );
        this.setTruncated( truncated );
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys( List<String> keys ) {
        this.keys = keys;
    }

    public void addKeys( List<String> keys ) {
        this.keys.addAll( keys );
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated( boolean truncated ) {
        this.truncated = truncated;
    }
}
