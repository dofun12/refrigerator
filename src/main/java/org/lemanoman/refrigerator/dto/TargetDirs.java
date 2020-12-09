package org.lemanoman.refrigerator.dto;

import java.io.File;

public class TargetDirs {
    private String rootPath;
    private String applicationPath;
    private String versionPath;

    private File rootDir;
    private File applicationDir;
    private File versionDir;

    public TargetDirs(String rootPath,String shortname){
        this.rootPath = rootPath;
        this.rootDir = new File(rootPath);

        this.applicationDir = new File(rootPath,shortname);

    }

    public TargetDirs(String rootPath,String shortname,String versionId){
        this.rootPath = rootPath;
        this.rootDir = new File(rootPath);

        if(rootPath==null){
            return;
        }

        this.applicationDir = new File(rootPath,shortname);
        if(!applicationDir.exists()){
            applicationDir.mkdirs();
        }

        this.versionDir = new File(applicationDir,versionId);
        if(!versionDir.exists()){
            versionDir.mkdirs();
        }

        this.applicationPath = applicationDir.getAbsolutePath();
        this.versionPath = applicationDir.getAbsolutePath();
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    public String getVersionPath() {
        return versionPath;
    }

    public void setVersionPath(String versionPath) {
        this.versionPath = versionPath;
    }

    public File getRootDir() {
        return rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public File getApplicationDir() {
        return applicationDir;
    }

    public void setApplicationDir(File applicationDir) {
        this.applicationDir = applicationDir;
    }

    public File getVersionDir() {
        return versionDir;
    }

    public void setVersionDir(File versionDir) {
        this.versionDir = versionDir;
    }
}
