package com.lms.userservice.adapter.out.persistence;

import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import com.lms.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.lms.userservice.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.lms.userservice.adapter.out.persistence.repository.UserJpaRepository;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository repository;

    @Mock
    private UserPersistenceMapper mapper;

    @InjectMocks
    private UserPersistenceAdapter adapter;

    @Test
    @DisplayName("save() - Translate DataIntegrityViolationException to BusinessException(USER_ALREADY_EXISTS)")
    void shouldTranslateDataIntegrityViolationExceptionToBusinessException() {
        // Given
        UserId userId = new UserId("user-1");
        User domainUser = User.builder().id(userId).email("test@mail.com").build();
        UserJpaEntity entity = new UserJpaEntity();

        when(mapper.toEntity(domainUser)).thenReturn(entity);

        // Giả lập infrastructure/JPA ném lỗi vi phạm constraint (VD: trùng email hoặc trùng ID)
        when(repository.saveAndFlush(entity)).thenThrow(
                new DataIntegrityViolationException("Database constraint violation")
        );

        // When
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adapter.save(domainUser),
                "Dự kiến ném ra BusinessException nhưng không có hoặc ném sai loại Exception"
        );

        // Then - Đảm bảo lỗi hạ tầng không bị leak và được translate chính xác
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());

        // Verify method calls
        verify(mapper).toEntity(domainUser);
        verify(repository).saveAndFlush(entity);
    }

    @Test
    @DisplayName("save() - Thành công, map dữ liệu đúng 2 chiều")
    void shouldSaveSuccessfully() {
        // Given
        UserId userId = new UserId("user-success");
        User domainUser = User.builder().id(userId).email("success@mail.com").build();

        UserJpaEntity entity = new UserJpaEntity();
        UserJpaEntity savedEntity = new UserJpaEntity();
        User savedDomainUser = User.builder().id(userId).email("success@mail.com").build();

        when(mapper.toEntity(domainUser)).thenReturn(entity);
        when(repository.saveAndFlush(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomainUser);

        // When
        User result = adapter.save(domainUser);

        // Then
        assertEquals(savedDomainUser, result);

        verify(mapper).toEntity(domainUser);
        verify(repository).saveAndFlush(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("save() - Lan truyền RuntimeException thông thường từ Repository, không translate")
    void shouldPropagateGenericRuntimeException() {
        // Given
        UserId userId = new UserId("user-error");
        User domainUser = User.builder().id(userId).email("error@mail.com").build();
        UserJpaEntity entity = new UserJpaEntity();

        when(mapper.toEntity(domainUser)).thenReturn(entity);
        when(repository.saveAndFlush(entity)).thenThrow(new RuntimeException("Database down"));

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adapter.save(domainUser)
        );

        // Then
        assertEquals("Database down", exception.getMessage());

        verify(mapper).toEntity(domainUser);
        verify(repository).saveAndFlush(entity);
    }

    @Test
    @DisplayName("save() - Lan truyền RuntimeException từ Mapper và không gọi Repository")
    void shouldPropagateMapperExceptionAndNotCallRepository() {
        // Given
        UserId userId = new UserId("user-mapper-error");
        User domainUser = User.builder().id(userId).email("mapper@mail.com").build();

        when(mapper.toEntity(domainUser)).thenThrow(new RuntimeException("Mapping failed"));

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adapter.save(domainUser)
        );

        // Then
        assertEquals("Mapping failed", exception.getMessage());

        verify(mapper).toEntity(domainUser);
        verify(repository, never()).saveAndFlush(any());
    }
}