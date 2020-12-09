package org.lemanoman.refrigerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.lemanoman.refrigerator.ConfigProperties;
import org.lemanoman.refrigerator.dto.ApplicationMetadata;
import org.lemanoman.refrigerator.dto.TargetDirs;
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
public class StoreService {

    private ConfigProperties configProperties;
    private ApplicationRepository applicationRepository;
    private VersionRepository versionRepository;
    private final Logger logger = LoggerFactory.getLogger(StoreService.class);

    public StoreService(ApplicationRepository applicationRepository, VersionRepository versionRepository, ConfigProperties configProperties){
        this.applicationRepository = applicationRepository;
        this.versionRepository = versionRepository;
        this.configProperties = configProperties;
    }


    public File getArtifactByVersion(String appShortname,String versionId){
        ApplicationModel applicationModel = applicationRepository.getByShortname(appShortname);

        VersionModel versionModel = versionRepository.getByApplicationIdAndVersionId(applicationModel.getId(),versionId);
        return new File(versionModel.getPath());
    }

    public File getLatestArtifact(String appShortname){
        ApplicationModel applicationModel = applicationRepository.getByShortname(appShortname);
        String lastVersionId = applicationModel.getLastVersion();

        VersionModel versionModel = versionRepository.getByApplicationIdAndVersionId(applicationModel.getId(),lastVersionId);
        return new File(versionModel.getPath());
    }

    public ApplicationModel createApplication(String shortname){
        ApplicationModel applicationModel = applicationRepository.getByShortname(shortname);
        if(applicationModel!=null && applicationModel.getId()!=null){
            return applicationModel;
        }
        applicationModel = new ApplicationModel();
        applicationModel.setName(shortname);
        applicationModel.setShortname(shortname);
        applicationModel.setDateAdded(now());
        applicationModel.setLastVersion(null);
        return applicationRepository.saveAndFlush(applicationModel);
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

    public VersionModel createVersion(String appShortName,String versionName,String filename,InputStream inputStream){
        ApplicationModel applicationModel = applicationRepository.getByShortname(appShortName);
        TargetDirs targetDirs = new TargetDirs(configProperties.getRootPath(),appShortName,versionName);

        File output = new File(targetDirs.getVersionDir(),filename);
        copyToFile(inputStream,output);
        VersionModel versionModel = new VersionModel();
        versionModel.setDateAdded(now());
        versionModel.setVersionId(versionName);
        versionModel.setApplicationId(applicationModel.getId());
        versionModel.setPath(output.getAbsolutePath());

        applicationModel.setLastVersion(versionName);
        applicationRepository.save(applicationModel);

        return versionRepository.saveAndFlush(versionModel);
    }

    private Date now(){
        return new Date();
    }
}
