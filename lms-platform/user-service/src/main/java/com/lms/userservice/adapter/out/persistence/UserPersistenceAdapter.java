package com.lms.userservice.adapter.out.persistence;

import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import com.lms.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.lms.userservice.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.lms.userservice.adapter.out.persistence.repository.UserJpaRepository;
import com.lms.userservice.application.port.out.UserRepositoryPort;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(UserId userId) {
        return repository.findById(userId.value())
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        try {
            UserJpaEntity entity = mapper.toEntity(user);
            // Dùng saveAndFlush để trigger SQL constraint check ngay lập tức
            UserJpaEntity savedEntity = repository.saveAndFlush(entity);
            return mapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            // Translate infrastructure exception to business exception
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "User profile already exists or violates uniqueness constraints");
        }
    }
}