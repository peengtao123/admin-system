package com.example.adminsystem.service;

import com.example.adminsystem.entity.Role;
import com.example.adminsystem.repository.RoleRepository;
import com.example.adminsystem.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role1;
    private Role role2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化测试数据
        role1 = new Role();
        role1.setId(1L);
        role1.setName("ADMIN");
        role1.setDescription("管理员角色");
        
        role2 = new Role();
        role2.setId(2L);
        role2.setName("USER");
        role2.setDescription("普通用户角色");
    }

    @Test
    void testFindAll() {
        // 准备测试数据
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> expectedPage = new PageImpl<>(roles, pageable, roles.size());
        
        // 模拟repository方法
        when(roleRepository.findAll(pageable)).thenReturn(expectedPage);
        
        // 调用service方法
        Page<Role> actualPage = roleService.findAll(pageable);
        
        // 验证结果
        assertNotNull(actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals("ADMIN", actualPage.getContent().get(0).getName());
        assertEquals("USER", actualPage.getContent().get(1).getName());
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindAllWithNullPageable() {
        // 调用service方法
        Page<Role> actualPage = roleService.findAll(null);
        
        // 验证结果
        assertNotNull(actualPage);
        assertEquals(0, actualPage.getTotalElements());
    }

    @Test
    @SuppressWarnings("null")
    void testFindById() {
        // 模拟repository方法
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(3L)).thenReturn(Optional.empty());
        
        // 测试存在的角色
        Role actualRole = roleService.findById(1L);
        assertNotNull(actualRole);
        assertEquals("ADMIN", actualRole.getName());
        
        // 测试不存在的角色
        Role nonExistentRole = roleService.findById(3L);
        assertNull(nonExistentRole);
        
        // 测试null参数
        Role nullRole = roleService.findById(null);
        assertNull(nullRole);
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(3L);
        verify(roleRepository, never()).findById(null);
    }

    @Test
    @SuppressWarnings("null")
    void testSave() {
        // 模拟repository方法
        when(roleRepository.save(role1)).thenReturn(role1);
        
        // 调用service方法
        Role savedRole = roleService.save(role1);
        
        // 验证结果
        assertNotNull(savedRole);
        assertEquals(1L, savedRole.getId());
        assertEquals("ADMIN", savedRole.getName());
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).save(role1);
    }

    @Test
    @SuppressWarnings("null")
    void testDeleteById() {
        // 调用service方法
        roleService.deleteById(1L);
        roleService.deleteById(null); // 应该被忽略
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).deleteById(1L);
        verify(roleRepository, never()).deleteById(null);
    }

    @Test
    void testFindAllList() {
        // 准备测试数据
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        
        // 模拟repository方法
        when(roleRepository.findAll()).thenReturn(roles);
        
        // 调用service方法
        List<Role> actualRoles = roleService.findAll();
        
        // 验证结果
        assertNotNull(actualRoles);
        assertEquals(2, actualRoles.size());
        assertEquals("ADMIN", actualRoles.get(0).getName());
        assertEquals("USER", actualRoles.get(1).getName());
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void testFindAllById() {
        // 准备测试数据
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        
        // 模拟repository方法
        when(roleRepository.findAllById(ids)).thenReturn(roles);
        
        // 测试正常情况
        List<Role> actualRoles = roleService.findAllById(ids);
        assertNotNull(actualRoles);
        assertEquals(2, actualRoles.size());
        
        // 测试null参数
        List<Role> nullRoles = roleService.findAllById(null);
        assertNotNull(nullRoles);
        assertEquals(0, nullRoles.size());
        
        // 测试空列表
        List<Role> emptyRoles = roleService.findAllById(new ArrayList<>());
        assertNotNull(emptyRoles);
        assertEquals(0, emptyRoles.size());
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).findAllById(ids);
        verify(roleRepository, never()).findAllById((Iterable<Long>) null);
    }

    @Test
    void testFindByName() {
        // 模拟repository方法
        when(roleRepository.findByName("ADMIN")).thenReturn(role1);
        when(roleRepository.findByName("NON_EXISTENT")).thenReturn(null);
        
        // 测试存在的角色
        Role actualRole = roleService.findByName("ADMIN");
        assertNotNull(actualRole);
        assertEquals("ADMIN", actualRole.getName());
        
        // 测试不存在的角色
        Role nonExistentRole = roleService.findByName("NON_EXISTENT");
        assertNull(nonExistentRole);
        
        // 验证repository方法被调用
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(roleRepository, times(1)).findByName("NON_EXISTENT");
    }
}
