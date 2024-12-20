package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.service.impl.EmployeeServiceImpl;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登陆")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);


        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @ApiOperation(value = "新增员工")
    @PostMapping
    public Result addEmp(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeServiceImpl.addEmp(employeeDTO);
        return Result.success();
    }

    @ApiOperation(value = "分页查询员工")
    @GetMapping("/page")
    public Result<PageResult> showEmpByPage(EmployeePageQueryDTO employeePageQueryDTO){
        PageResult pr = employeeServiceImpl.showEmpByPage(employeePageQueryDTO);
        return Result.success(pr);
    }

    @ApiOperation(value="账号启用和禁用")
    @PostMapping("/status/{status}")
    public Result accountAvaliable(@PathVariable Integer status,@RequestParam Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeServiceImpl.accountAvaliable(status,id);
        return Result.success();
    }

    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result editEmp(@RequestBody EmployeeDTO employeeDTO){
        employeeServiceImpl.editEmp(employeeDTO);
        return Result.success();
    }

    @ApiOperation("根据id查询员工")
    @GetMapping("/{id}")
    public Result showById(@PathVariable Integer id){
        EmployeeDTO employeeDTO = employeeServiceImpl.showById(id);
        return Result.success(employeeDTO);
    }

}
