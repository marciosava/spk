package com.spk.sistemas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spk.sistemas.repository.RoleRepository;
import com.spk.sistemas.service.RoleService;
import com.spk.sistemas.model.Role;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
