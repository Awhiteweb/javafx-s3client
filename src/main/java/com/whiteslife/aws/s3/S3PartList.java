package com.whiteslife.aws.s3;

import java.util.stream.Stream;

public class S3PartList {
    private Stream<S3Part> partStream;
    private boolean truncated;
    private Integer nextPartNumberMarker;

    public S3PartList( Stream<S3Part> partStream, boolean isTruncated, Integer nextPartNumberMarker ) {
        this.partStream = partStream;
        this.truncated = isTruncated;
        this.nextPartNumberMarker = nextPartNumberMarker;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public Integer getNextPartNumberMarker() {
        return nextPartNumberMarker;
    }

    public Stream<S3Part> getPartStream() {
        return partStream;
    }
}
