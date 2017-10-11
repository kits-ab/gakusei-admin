package se.kits.gakusei.gakuseiadmin.unit.controllers;

import com.sun.org.apache.regexp.internal.RE;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.kits.gakusei.gakuseiadmin.content.AdminUserRepository;
import se.kits.gakusei.gakuseiadmin.controllers.AdminUserController;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;
import se.kits.gakusei.user.model.Event;
import se.kits.gakusei.user.model.User;
import se.kits.gakusei.user.repository.EventRepository;

import java.security.Principal;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminUserControllerTest {

    @InjectMocks
    private AdminUserController adminUserController;

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private Principal principal;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = AdminTestTools.createUser();

        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenReturn(null);
        Mockito.when(principal.getName()).thenReturn(testUser.getUsername());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("encoded" + testUser.getPassword());
    }

    @Test
    public void searchUserFilterByRole() throws Exception {
    }

    @Test
    public void resetPasswordOK() throws Exception {
        Mockito.when(adminUserRepository.findOne(testUser.getUsername())).thenReturn(testUser);
        testUser.setPassword("newPassword");
        Mockito.when(adminUserRepository.save(testUser)).thenReturn(testUser);

        ResponseEntity<User> re = adminUserController.resetPassword(testUser, principal);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(testUser, re.getBody());
    }

    @Test
    public void resetPasswordNotFound() {
        Mockito.when(adminUserRepository.findOne(testUser.getUsername())).thenReturn(null);

        ResponseEntity<User> re = adminUserController.resetPassword(testUser, principal);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

    @Test
    public void changeRoleOK() throws Exception {
        Mockito.when(adminUserRepository.findOne(testUser.getUsername())).thenReturn(testUser);
        testUser.setRole("newRole");
        Mockito.when(adminUserRepository.save(testUser)).thenReturn(testUser);

        ResponseEntity<User> re = adminUserController.changeRole(testUser, principal);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(testUser, re.getBody());
    }

    @Test
    public void changeRoleNotFound() {
        Mockito.when(adminUserRepository.findOne(testUser.getUsername())).thenReturn(null);

        ResponseEntity<User> re = adminUserController.changeRole(testUser, principal);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

    @Test
    public void deleteUserOK() throws Exception {
        Mockito.when(adminUserRepository.exists(testUser.getUsername())).thenReturn(true);

        ResponseEntity<User> re = adminUserController.deleteUser(testUser.getUsername(), principal);

        assertEquals(HttpStatus.OK, re.getStatusCode());
    }

    @Test
    public void deleteUserNotFound() {
        Mockito.when(adminUserRepository.exists(testUser.getUsername())).thenReturn(false);

        ResponseEntity<User> re = adminUserController.deleteUser(testUser.getUsername(), principal);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

}