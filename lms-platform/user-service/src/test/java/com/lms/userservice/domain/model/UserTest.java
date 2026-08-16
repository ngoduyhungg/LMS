package com.lms.userservice.domain.model;

import com.lms.userservice.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // ==========================================
    // TEST CASES FOR UserId (Value Object)
    // ==========================================

    @Test
    @DisplayName("1. UserId - Nên khởi tạo thành công khi value hợp lệ")
    void shouldCreateUserId_whenValueIsValid() {
        String validId = "d2b8e3a2-1234-abcd";
        UserId userId = new UserId(validId);
        assertEquals(validId, userId.value());
    }

    @Test
    @DisplayName("2. UserId - Nên ném ngoại lệ IllegalArgumentException khi value bị null")
    void shouldThrowException_whenUserIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new UserId(null));
        assertEquals("UserId cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("3. UserId - Nên ném ngoại lệ IllegalArgumentException khi value là chuỗi rỗng")
    void shouldThrowException_whenUserIdIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new UserId(""));
        assertEquals("UserId cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("4. UserId - Nên ném ngoại lệ IllegalArgumentException khi value chỉ chứa khoảng trắng")
    void shouldThrowException_whenUserIdIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new UserId("   "));
        assertEquals("UserId cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("5. UserId - Record equality (Hai đối tượng UserId cùng value phải equal)")
    void shouldBeEqual_whenUserIdValuesAreSame() {
        UserId id1 = new UserId("same-id");
        UserId id2 = new UserId("same-id");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    // ==========================================
    // TEST CASES FOR User (Aggregate Root)
    // ==========================================

    @Test
    @DisplayName("6. User - Nên khởi tạo thành công qua Builder với đầy đủ các field")
    void shouldCreateUser_withAllFields() {
        UserId id = new UserId("123");
        User user = User.builder()
                .id(id)
                .email("test@mail.com")
                .fullName("Test User")
                .avatarUrl("http://avatar.com")
                .phoneNumber("0123456789")
                .status(UserStatus.ACTIVE)
                .build();

        assertNotNull(user);
        assertEquals("123", user.getId().value());
        assertEquals("test@mail.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals("http://avatar.com", user.getAvatarUrl());
        assertEquals("0123456789", user.getPhoneNumber());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("7. User - Nên khởi tạo thành công khi thiếu các field optional (avatarUrl, phoneNumber)")
    void shouldCreateUser_withoutOptionalFields() {
        UserId id = new UserId("123");
        User user = User.builder()
                .id(id)
                .email("test@mail.com")
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .build();

        assertNull(user.getAvatarUrl());
        assertNull(user.getPhoneNumber());
        assertEquals("Test User", user.getFullName());
    }

    @Test
    @DisplayName("8. User - Nên lưu trữ chính xác trạng thái SUSPENDED")
    void shouldRetainSuspendedStatus() {
        User user = User.builder().status(UserStatus.SUSPENDED).build();
        assertEquals(UserStatus.SUSPENDED, user.getStatus());
    }

    @Test
    @DisplayName("9. User - Nên lưu trữ chính xác trạng thái INACTIVE")
    void shouldRetainInactiveStatus() {
        User user = User.builder().status(UserStatus.INACTIVE).build();
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("10. User - Các thao tác getter cơ bản không làm biến đổi dữ liệu")
    void shouldNotMutateDataOnGetter() {
        String originalEmail = "immutable@mail.com";
        User user = User.builder().email(originalEmail).build();
        assertEquals(originalEmail, user.getEmail());
    }
}