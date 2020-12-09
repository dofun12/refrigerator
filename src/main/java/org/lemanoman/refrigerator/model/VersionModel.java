package org.lemanoman.refrigerator.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "version",uniqueConstraints={@UniqueConstraint(columnNames = {"versionId" })})
public class VersionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique=true)
    private String versionId;

    @Column
    private String path;

    @Column(nullable = false)
    private Integer applicationId;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    public Integer getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
