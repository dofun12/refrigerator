package org.lemanoman.refrigerator.dto;

import java.util.List;

public class ApplicationMetadata {
    private String applicationName;
    private String basePath;
    private String dateCreated;
    private List<VersionMetadata> versions;

    public List<VersionMetadata> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionMetadata> versions) {
        this.versions = versions;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
