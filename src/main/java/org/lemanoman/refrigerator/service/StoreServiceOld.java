package org.lemanoman.refrigerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.lemanoman.refrigerator.ConfigProperties;
import org.lemanoman.refrigerator.dto.ApplicationMetadata;
import org.lemanoman.refrigerator.dto.VersionMetadata;
import org.lemanoman.refrigerator.model.ApplicationModel;
import org.lemanoman.refrigerator.model.VersionModel;
import org.lemanoman.refrigerator.repository.ApplicationRepository;
import org.lemanoman.refrigerator.repository.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class StoreServiceOld {

    private ConfigProperties configProperties;
    private ApplicationRepository applicationRepository;
    private VersionRepository versionRepository;

    public StoreServiceOld(ApplicationRepository applicationRepository, VersionRepository versionRepository, ConfigProperties configProperties){
        this.applicationRepository = applicationRepository;
        this.versionRepository = versionRepository;
        this.configProperties = configProperties;
    }

    Logger logger = LoggerFactory.getLogger(StoreServiceOld.class);


    final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public String getRootPath() {
        return configProperties.getRootPath();
    }

    public File getLatestFileByApplicationName(String applicationVersion) {
        ApplicationModel applicationMetadata = applicationRepository.getByShortname(applicationVersion);
        if (applicationMetadata == null) {
            return null;
        }
        List<VersionModel> versions = versionRepository.findAllByApplicationId(applicationMetadata.getId());
        VersionModel versionMetadata = findLatestVersionMetadata(versions);
        if (versionMetadata == null || versionMetadata.getPath().isEmpty()) {
            return null;
        }
        File file = new File(versionMetadata.getPath());
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    public File getFileByApplicationNameAndVersion(String applicationVersion, String versionName) {
        ApplicationModel applicationMetadata = applicationRepository.getByShortname(applicationVersion);
        if (applicationMetadata == null) {
            logger.info("Application not founded");
            return null;
        }
        VersionModel versionMetadata = versionRepository.getByApplicationIdAndVersionId(applicationMetadata.getId(),versionName);
        if (versionMetadata == null || versionMetadata.getPath().isEmpty()) {
            logger.info("Version not founded");
            return null;
        }
        File file = new File(versionMetadata.getPath());
        if (!file.exists()) {
            logger.info("VersionFile not founded " + versionMetadata.getPath());
            return null;
        }
        return file;
    }

    private File getApplicationBasePath(String applicationName) {
        return new File(getRootPath(), applicationName);
    }

    private File getApplicationMetadataFile(String applicationName) {
        File applicationPath = getApplicationBasePath(applicationName);
        return new File(applicationPath, "application.json");
    }

    private ApplicationMetadata getApplicationMetadata(String applicationName) {
        File applicationMetadataFile = getApplicationMetadataFile(applicationName);
        if (!applicationMetadataFile.exists()) return null;
        try {
            return mapper.readValue(applicationMetadataFile, ApplicationMetadata.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveApplicationMetadata(String applicationName, ApplicationMetadata applicationMetadata) {
        try {
            mapper.writeValue(getApplicationMetadataFile(applicationName), applicationMetadata);
        } catch (IOException e) {
            logger.error("Error saving the aplicationMetadata " + applicationName, e);
        }
    }


    private String getNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public void createApplication(String name) {
        File rootFile = new File(getRootPath());
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            return;
        }
        File applicationBasePath = getApplicationBasePath(name);

        if (!applicationBasePath.exists() && !applicationBasePath.mkdirs()) {
            logger.error("Erro ao criar os dirs " + applicationBasePath.getAbsolutePath());
            return;
        }

        ApplicationMetadata metadata = getApplicationMetadata(name);
        if (metadata != null) {
            logger.error("Erro ao fazer o parse do applicationMetadata " + name);
            return;
        }
        ApplicationMetadata applicationMetadata = new ApplicationMetadata();
        applicationMetadata.setApplicationName(name);
        applicationMetadata.setBasePath(getApplicationBasePath(name).getAbsolutePath());
        applicationMetadata.setDateCreated(getNow());
        saveApplicationMetadata(name, applicationMetadata);

    }

    private void copyToFile(InputStream inputStream, File outputFile) {
        try {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[1048576];

            int count;
            while ((count = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
            logger.debug("Dando um flush");

            bis.close();
            inputStream.close();

            bos.flush();
            bos.close();
            fileOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public VersionMetadata createVersion(String applicationName, String versionName, String fileName, InputStream fileInputstream) {
        File rootFile = new File(getRootPath());
        File applicationPath = new File(rootFile, applicationName);
        File versionPath = new File(applicationPath, versionName);
        if (!versionPath.exists() && !versionPath.mkdirs()) {
            return null;
        }
        File destinationFile = new File(versionPath, fileName);


        VersionMetadata versionMetadata = new VersionMetadata();
        versionMetadata.setDateCreated(getNow());
        versionMetadata.setDateUpdated(getNow());
        versionMetadata.setPath(destinationFile.getAbsolutePath());
        versionMetadata.setVersion(versionName);

        ApplicationMetadata metadata = getApplicationMetadata(applicationName);

        copyToFile(fileInputstream, destinationFile);

        File versionMetadataFile = new File(versionPath, "version.json");
        try {
            mapper.writeValue(versionMetadataFile, versionMetadata);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (metadata == null) {
            return null;
        }

        if (metadata.getVersions() == null) {
            metadata.setVersions(new ArrayList<>());
        }
        List<VersionMetadata> list = metadata.getVersions();
        VersionMetadata oldVersionMetadata = findVersionMetadata(versionName, metadata.getVersions());
        if (!list.isEmpty() && oldVersionMetadata != null) {
            versionMetadata.setPath(destinationFile.getAbsolutePath());
            versionMetadata.setDateCreated(oldVersionMetadata.getDateCreated());
            versionMetadata.setDateUpdated(getNow());
            list = list.stream().filter(v -> !v.getVersion().equals(versionName)).collect(Collectors.toList());
        }
        list.add(versionMetadata);
        metadata.setVersions(list);
        saveApplicationMetadata(applicationName, metadata);

        return versionMetadata;
    }

    private Integer sumVersion(String version) {
        version = version.replaceAll("\\.", "");
        version = version.replaceAll("\\-", "");
        return Integer.parseInt(version);
    }

    private VersionModel findLatestVersionMetadata(List<VersionModel> versions) {
        VersionModel out = versions.stream().max(Comparator.comparingInt(o -> sumVersion(o.getVersionId()))).orElse(null);
        return out;
    }

    private VersionMetadata findVersionMetadata(String versionName, List<VersionMetadata> versions) {
        return versions.stream().filter(versionMetadata -> versionMetadata.getVersion().equals(versionName)).findFirst().orElse(null);
    }

}
