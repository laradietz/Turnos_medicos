package com.clinica.mapper;

import com.clinica.dto.*;
import com.clinica.entity.*;
import org.mapstruct.*;

import java.util.List;

// ── NOTIFICATION MAPPER ──────────────────────────────────────
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type",    expression = "java(n.getType().name())")
    @Mapping(target = "channel", expression = "java(n.getChannel().name())")
    NotificationDtos.NotificationResponse toResponse(Notification n);

    List<NotificationDtos.NotificationResponse> toResponseList(List<Notification> list);
}
