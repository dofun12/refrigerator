package org.lemanoman.refrigerator.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lemanoman.refrigerator.model.ApplicationModel;
import org.lemanoman.refrigerator.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
class ApplicationRepositoryTests {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    void testValidCrud() {
        ApplicationModel model = new ApplicationModel();
        model.setName("Um novo programa");
        model.setShortname("myappid");
        model.setLastVersion("latest");
        model.setDateAdded(new Date());

        ApplicationModel saved = applicationRepository.saveAndFlush(model);

        ApplicationModel recovered = applicationRepository.getOne(saved.getId());
        Assertions.assertNotNull(recovered);
        Assertions.assertEquals(model.getName(), recovered.getName());
        Assertions.assertEquals(model.getLastVersion(), recovered.getLastVersion());
        Assertions.assertEquals(model.getDateAdded(), recovered.getDateAdded());
        Assertions.assertEquals(model.getShortname(), recovered.getShortname());

        final String newName = "Novo Nome";
        saved.setName(newName);

        ApplicationModel updated = applicationRepository.getOne(saved.getId());
        Assertions.assertEquals(updated.getName(),newName);

        applicationRepository.deleteById(saved.getId());
        ApplicationModel afterDeleteApplication = applicationRepository.findById(saved.getId()).orElse(null);
        Assertions.assertNull(afterDeleteApplication);
    }


    @Test
    void testForDuplicatedAppId() {
        ApplicationModel model = new ApplicationModel();
        model.setName("Um novo programa");
        model.setShortname("myappid");
        model.setLastVersion("latest");
        model.setDateAdded(new Date());

        applicationRepository.saveAndFlush(model);

        ApplicationModel duplice = new ApplicationModel();
        duplice.setName("Outro Programa");
        duplice.setShortname("myappid");
        duplice.setLastVersion("latest");
        duplice.setDateAdded(new Date());

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> applicationRepository.saveAndFlush(duplice));


    }

}
