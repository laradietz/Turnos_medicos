package com.clinica.mapper;

import com.clinica.dto.NotificationDtos;
import com.clinica.entity.Notification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T23:11:31-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDtos.NotificationResponse toResponse(Notification n) {
        if ( n == null ) {
            return null;
        }

        UUID id = null;
        String subject = null;
        Boolean isRead = null;
        LocalDateTime sentAt = null;
        LocalDateTime createdAt = null;

        id = n.getId();
        subject = n.getSubject();
        isRead = n.getIsRead();
        sentAt = n.getSentAt();
        createdAt = n.getCreatedAt();

        String type = n.getType().name();
        String channel = n.getChannel().name();

        NotificationDtos.NotificationResponse notificationResponse = new NotificationDtos.NotificationResponse( id, type, channel, subject, isRead, sentAt, createdAt );

        return notificationResponse;
    }

    @Override
    public List<NotificationDtos.NotificationResponse> toResponseList(List<Notification> list) {
        if ( list == null ) {
            return null;
        }

        List<NotificationDtos.NotificationResponse> list1 = new ArrayList<NotificationDtos.NotificationResponse>( list.size() );
        for ( Notification notification : list ) {
            list1.add( toResponse( notification ) );
        }

        return list1;
    }
}
