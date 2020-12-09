package org.lemanoman.refrigerator.dto;

import org.lemanoman.refrigerator.model.VersionModel;

import java.util.List;

public class ApplicationJS {
    private String appShortName;
    private String latestVersion;
    private List<VersionJS> versions;

    public String getAppShortName() {
        return appShortName;
    }

    public void setAppShortName(String appShortName) {
        this.appShortName = appShortName;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public List<VersionJS> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionJS> versions) {
        this.versions = versions;
    }
}
