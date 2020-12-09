package org.lemanoman.refrigerator.repository;

import org.lemanoman.refrigerator.model.ApplicationModel;
import org.lemanoman.refrigerator.model.VersionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<VersionModel, Integer> {

    List<VersionModel> findAllByApplicationId(Integer applicationId);

    VersionModel getByApplicationIdAndVersionId(Integer applicationId,String versionId);
}
