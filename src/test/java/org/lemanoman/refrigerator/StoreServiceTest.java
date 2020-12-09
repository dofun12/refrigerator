package org.lemanoman.refrigerator;

import org.junit.jupiter.api.Test;
import org.lemanoman.refrigerator.service.StoreServiceOld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class StoreServiceTest {

    @Autowired
    private StoreServiceOld storeService;

    @Test
    public void doValidStorage(){

        final String applicationName = "videoviz-backend";
        storeService.createApplication(applicationName);

        File inputFile = new File("E:\\projetos\\videoviz\\videoviz-backend\\target\\videoviz-backend-0.0.3-SNAPSHOT.jar");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        storeService.createVersion(applicationName,"0.0.3",inputFile.getName(),fis);
        storeService.createVersion(applicationName,"0.0.4",inputFile.getName(),fis);
        storeService.createVersion(applicationName,"0.0.5",inputFile.getName(),fis);

    }

}

