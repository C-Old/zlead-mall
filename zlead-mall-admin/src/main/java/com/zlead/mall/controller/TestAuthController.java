package com.zlead.mall.controller;

import com.zlead.mall.dto.CommonResult;
import com.zlead.mall.model.UmsAdmin;
import com.zlead.mall.service.UmsAdminService;
import com.zlead.mall.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Api(tags = "TestAuthController", description = "测试权限")
@RestController
@RequestMapping(value = "test")
public class TestAuthController {
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("查找")
    @RequestMapping(value = "query", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('test:auth:query')")
    public Object query(Principal principal) {
        UmsAdmin umsAdmin = (UmsAdmin) redisUtil.get(principal.getName());
        return new CommonResult().success(umsAdmin);
    }

    @ApiOperation("删除")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('test:auth:delete')")
    public Object delete() {
        return new CommonResult().success("delete");
    }

    @ApiOperation("修改")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('test:auth:update')")
    public Object update() {
        return new CommonResult().success("update");
    }

    @ApiOperation("插入")
    @RequestMapping(value = "insert", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('test:auth:insert')")
    public Object insert(Principal principal) {
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        redisUtil.set(principal.getName(), umsAdmin);
        return new CommonResult().success("insert");
    }
}
