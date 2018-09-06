package com.bootdo.common.dao;

import com.bootdo.common.domain.SysAttachment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangfei on 2016/4/25.
 */
@Repository
public interface SysAttachmentDao extends PagingAndSortingRepository<SysAttachment, Long>, JpaSpecificationExecutor<SysAttachment> {
    @Query("select u from SysAttachment u where u.persistedFileName like %?1%")
    List<SysAttachment> findByPersistedFileNameLike(String wholeMd5);

    List<SysAttachment> findByFileMd5(String wholeMd5);

    List<SysAttachment> findByIdIn(Long[] ids);

    List<SysAttachment> findByParentId(Long parentId);
}
