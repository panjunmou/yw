package com.bootdo.common.dao;

import com.bootdo.common.domain.SysAttachmentRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangfei on 2016/4/25.
 */
@Repository
public interface SysAttachmentRoleDao extends PagingAndSortingRepository<SysAttachmentRole, Long>, JpaSpecificationExecutor<SysAttachmentRole> {

    @Modifying
    int deleteByAttactmentIdIn(List<Long> attactmentIdList);
}
