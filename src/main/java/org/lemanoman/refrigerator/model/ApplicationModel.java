package org.lemanoman.refrigerator.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "application",uniqueConstraints={@UniqueConstraint(columnNames = {"shortname" })})
public class ApplicationModel  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(unique=true)
    private String shortname;


    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    @Column
    private String lastVersion;

    private String name;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
}
