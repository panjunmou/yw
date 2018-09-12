package com.bootdo.common.dao;

import com.bootdo.common.domain.SysAttachmentRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangfei on 2016/4/25.
 */
@Repository
public interface SysAttachmentRoleDao extends PagingAndSortingRepository<SysAttachmentRole, Long>, JpaSpecificationExecutor<SysAttachmentRole> {
}
