package com.lms.userservice.application.service;

import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import com.lms.userservice.application.port.in.command.SyncUserCommand;
import com.lms.userservice.application.port.out.UserRepositoryPort;
import com.lms.userservice.domain.enums.UserStatus;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UserApplicationService userApplicationService;

    // ==========================================
    // GET INTERNAL PROFILE TESTS
    // ==========================================

    @Test
    @DisplayName("1. getInternalProfile - Thành công khi profile tồn tại")
    void getInternalProfile_Success_WhenUserExists() {
        UserId userId = new UserId("user-1");
        User mockUser = User.builder().id(userId).email("a@a.com").build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userApplicationService.getInternalProfile(userId);
        assertNotNull(result);
        assertEquals("user-1", result.getId().value());
    }

    @Test
    @DisplayName("2. getInternalProfile - Ném USER_NOT_FOUND với format message chứa ID thật khi profile không tồn tại (FIX BUG 1)")
    void getInternalProfile_ThrowsException_WhenUserNotFound() {
        // Sử dụng đúng ID từ log E2E để test
        String rawId = "e2432dfe-91ad-4324-a1b7-1baee2e2885a";
        UserId userId = new UserId(rawId);

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userApplicationService.getInternalProfile(userId));

        // Verify ErrorCode
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        // Verify Message format (Đảm bảo không còn %s mà chứa ID thực tế)
        String expectedMessage = String.format(ErrorCode.USER_NOT_FOUND.getMessage(), rawId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ==========================================
    // JIT PROVISIONING (SYNC PROFILE) TESTS
    // ==========================================

    @Test
    @DisplayName("3. syncProfile - Không lưu mới và trả về User cũ khi Profile đã tồn tại")
    void syncProfile_ReturnsExistingUser_WhenProfileAlreadyExists() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", "Name", null);
        User existingUser = User.builder().id(new UserId("user-1")).build();

        when(userRepositoryPort.findById(new UserId("user-1"))).thenReturn(Optional.of(existingUser));

        User result = userApplicationService.syncProfile(command);

        assertNotNull(result);
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("4. syncProfile - Lưu thành công và trả về User mới với status ACTIVE khi Profile chưa có")
    void syncProfile_SavesAndReturnsNewUser_WhenProfileDoesNotExist() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", "Name", null);

        when(userRepositoryPort.findById(new UserId("user-1"))).thenReturn(Optional.empty());
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userApplicationService.syncProfile(command);

        assertNotNull(result);
        assertEquals(UserStatus.ACTIVE, result.getStatus());
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    // ==========================================
    // VALIDATION TESTS
    // ==========================================

    @Test
    @DisplayName("5. syncProfile - Ném ngoại lệ khi userId bị null")
    void syncProfile_ThrowsException_WhenUserIdIsNull() {
        SyncUserCommand command = new SyncUserCommand(null, "a@a.com", "Name", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userApplicationService.syncProfile(command));
        assertEquals(ErrorCode.ILLEGAL_ARGUMENT, ex.getErrorCode());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("6. syncProfile - Ném ngoại lệ khi email bị trống")
    void syncProfile_ThrowsException_WhenEmailIsBlank() {
        SyncUserCommand command = new SyncUserCommand("user-1", "  ", "Name", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userApplicationService.syncProfile(command));
        assertEquals(ErrorCode.ILLEGAL_ARGUMENT, ex.getErrorCode());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("7. syncProfile - Ném ngoại lệ khi fullName bị null")
    void syncProfile_ThrowsException_WhenFullNameIsNull() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", null, null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userApplicationService.syncProfile(command));
        assertEquals(ErrorCode.ILLEGAL_ARGUMENT, ex.getErrorCode());
        verify(userRepositoryPort, never()).save(any());
    }

    // ==========================================
    // RACE CONDITION & ERROR HANDLING TESTS
    // ==========================================

    @Test
    @DisplayName("8. syncProfile (Race Condition) - Xử lý êm đẹp khi USER_ALREADY_EXISTS và tìm lại được user")
    void syncProfile_HandlesRaceCondition_ReturnsExistingUser() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", "Name", null);
        UserId userId = new UserId("user-1");
        User existingUser = User.builder().id(userId).build();

        // 1. First find returns empty
        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.empty()) // Lần gọi 1
                .thenReturn(Optional.of(existingUser)); // Lần gọi 2 (recovery)

        // 2. Save throws constraint violation translated to business exception
        when(userRepositoryPort.save(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.USER_ALREADY_EXISTS));

        User result = userApplicationService.syncProfile(command);

        assertNotNull(result);
        verify(userRepositoryPort, times(2)).findById(userId);
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("9. syncProfile (Race Condition Fail) - Ném lại lỗi nếu USER_ALREADY_EXISTS nhưng không tìm thấy user")
    void syncProfile_PropagatesException_WhenRaceConditionFailsToRecover() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", "Name", null);
        UserId userId = new UserId("user-1");

        // 1. Cả 2 lần findById đều trả về empty
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        // 2. Save throws error
        when(userRepositoryPort.save(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.USER_ALREADY_EXISTS));

        BusinessException ex = assertThrows(BusinessException.class, () -> userApplicationService.syncProfile(command));

        assertEquals(ErrorCode.USER_ALREADY_EXISTS, ex.getErrorCode()); // Không biến thành fake success
        verify(userRepositoryPort, times(2)).findById(userId);
    }

    @Test
    @DisplayName("10. syncProfile - KHÔNG nuốt các ngoại lệ business khác ngoài USER_ALREADY_EXISTS")
    void syncProfile_PropagatesOtherBusinessExceptions() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", "Name", null);

        when(userRepositoryPort.findById(new UserId("user-1"))).thenReturn(Optional.empty());
        when(userRepositoryPort.save(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        BusinessException ex = assertThrows(BusinessException.class, () -> userApplicationService.syncProfile(command));

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, ex.getErrorCode());
        // findById chỉ gọi 1 lần, không kích hoạt cơ chế recovery
        verify(userRepositoryPort, times(1)).findById(new UserId("user-1"));
    }

    @Test
    @DisplayName("11. syncProfile - Lỗi DB thông thường (RuntimeException) phải được lan truyền")
    void syncProfile_PropagatesGenericRuntimeExceptions() {
        SyncUserCommand command = new SyncUserCommand("user-1", "a@a.com", "Name", null);

        when(userRepositoryPort.findById(new UserId("user-1"))).thenReturn(Optional.empty());
        when(userRepositoryPort.save(any(User.class))).thenThrow(new RuntimeException("Database down"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userApplicationService.syncProfile(command));

        assertEquals("Database down", ex.getMessage());
    }
    @Test
    @DisplayName("12. getBatchInternalProfiles - Trả về danh sách khi truyền vào Set IDs hợp lệ")
    void getBatchInternalProfiles_ReturnsList_WhenIdsValid() {
        Set<UserId> userIds = Set.of(new UserId("uuid-1"), new UserId("uuid-2"));
        List<User> mockUsers = List.of(
                User.builder().id(new UserId("uuid-1")).email("student1@mail.com").build(),
                User.builder().id(new UserId("uuid-2")).email("student2@mail.com").build()
        );

        when(userRepositoryPort.findByIds(userIds)).thenReturn(mockUsers);

        List<User> result = userApplicationService.getBatchInternalProfiles(userIds);

        assertEquals(2, result.size());
        verify(userRepositoryPort, times(1)).findByIds(userIds);
    }

    @Test
    @DisplayName("13. getBatchInternalProfiles - Trả về list rỗng khi đầu vào rỗng (không gọi DB)")
    void getBatchInternalProfiles_ReturnsEmptyList_WhenInputEmpty() {
        List<User> result = userApplicationService.getBatchInternalProfiles(Set.of());

        assertTrue(result.isEmpty());
        verify(userRepositoryPort, never()).findByIds(any());
    }
}