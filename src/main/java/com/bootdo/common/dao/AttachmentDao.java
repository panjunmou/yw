package com.bootdo.common.dao;

import com.bootdo.common.domain.Attachment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangfei on 2016/4/25.
 */
@Repository
public interface AttachmentDao extends PagingAndSortingRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {
    @Query("select u from Attachment u where u.persistedFileName like %?1%")
    List<Attachment> findByPersistedFileNameLike(String wholeMd5);

    List<Attachment> findByFileMd5(String wholeMd5);

    List<Attachment> findByIdIn(Long[] ids);
}
