package org.lemanoman.refrigerator.repository;

import org.lemanoman.refrigerator.model.ApplicationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationModel, Integer> {
    public ApplicationModel getByShortname(String shortname);
}
