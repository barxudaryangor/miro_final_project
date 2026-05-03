package com.example.audit.api.mapper;

import com.example.audit.api.dto.AuditLogResponse;
import com.example.audit.persistence.entity.AuditLogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLogEntity entity);
}
