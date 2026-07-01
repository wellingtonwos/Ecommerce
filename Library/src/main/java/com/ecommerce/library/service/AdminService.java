package com.ecommerce.library.service;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;

public interface AdminService {

    Admin findAdminByUsername(String username);
    Admin save(AdminDto adminDto);

}
