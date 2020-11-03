package com.whiteslife.mocks;

import java.util.List;

public class MockResponse {
    private String startAfter;
    private List<String> names;
    private boolean truncated;

    public MockResponse( String startAfter, List<String> names, boolean isTruncated ) {
        this.startAfter = startAfter;
        this.names = names;
        this.truncated = isTruncated;
    }

    public String getStartAfter() {
        return startAfter;
    }

    public void setStartAfter( String startAfter ) {
        this.startAfter = startAfter;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames( List<String> names ) {
        this.names = names;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated( boolean truncated ) {
        this.truncated = truncated;
    }
}
