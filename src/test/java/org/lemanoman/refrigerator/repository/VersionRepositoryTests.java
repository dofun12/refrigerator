package org.lemanoman.refrigerator.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lemanoman.refrigerator.model.ApplicationModel;
import org.lemanoman.refrigerator.model.VersionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
class VersionRepositoryTests {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private VersionRepository versionRepository;

    private Integer createDefaultApplication(){
        ApplicationModel model = new ApplicationModel();
        model.setName("Um novo programa");
        model.setShortname("myappid");
        model.setLastVersion("latest");
        model.setDateAdded(new Date());

        ApplicationModel saved = applicationRepository.saveAndFlush(model);
        return saved.getId();
    }

    private VersionModel createVersionModel(String versionId, String path, Integer applicationId) {
        VersionModel versionModel = new VersionModel();
        versionModel.setApplicationId(applicationId);
        versionModel.setDateAdded(new Date());
        versionModel.setVersionId(versionId);
        versionModel.setPath(path);
        return versionModel;
    }

    @Test
    void testValidCrud() {
        Integer applicationId = createDefaultApplication();
        VersionModel model = createVersionModel("v0.0.20", "/home/kevim/file.jar", applicationId);
        VersionModel saved = versionRepository.saveAndFlush(model);

        VersionModel retrievedVersion = versionRepository.getOne(saved.getId());
        Assertions.assertEquals(model.getApplicationId(),retrievedVersion.getApplicationId());
        Assertions.assertEquals(model.getDateAdded(),retrievedVersion.getDateAdded());
        Assertions.assertEquals(model.getVersionId(),retrievedVersion.getVersionId());
        Assertions.assertEquals(model.getPath(),retrievedVersion.getPath());

        versionRepository.deleteById(retrievedVersion.getId());
        Assertions.assertNull(versionRepository.findById(retrievedVersion.getId()).orElse(null));
    }

    @Test
    void testValidInsertAndList() {
        Integer applicationId = createDefaultApplication();


        versionRepository.save(createVersionModel("v0.0.20", "/home/kevim/file.jar", applicationId));
        versionRepository.save(createVersionModel("v0.0.21", "/home/kevim/file2.jar", applicationId));
        versionRepository.save(createVersionModel("v0.0.23", "/home/kevim/file3.jar", applicationId));
        versionRepository.flush();


        List<VersionModel> list = versionRepository.findAllByApplicationId(applicationId);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(3,list.size());

    }


    @Test
    void testForDuplicatedVersionId() {
        Integer applicationId = createDefaultApplication();

        versionRepository.save(createVersionModel("v0.0.20", "/home/kevim/file.jar", applicationId));
        versionRepository.save(createVersionModel("v0.0.20", "/home/kevim/file.jar", applicationId));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> applicationRepository.flush());


    }

}
