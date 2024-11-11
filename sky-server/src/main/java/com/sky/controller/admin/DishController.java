package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@Slf4j
@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;
    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO){
        dishService.addDish(dishDTO);
        clearRedis("dish_"+dishDTO.getCategoryId());
        return Result.success();
    }

    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> showDishByPages(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.showByPages(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) throws Exception {
        dishService.deleteById(ids);
        clearRedis("dish_*");
        return Result.success();

    }

    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> selectById(@PathVariable Long id){
        DishVO dishVO = dishService.selectById(id);
        return Result.success(dishVO);
    }

    @ApiOperation("根据id修改菜品")
    @PutMapping
    public Result updateDishById(@RequestBody DishDTO dishDTO){
        dishService.updateDishById(dishDTO);
        clearRedis("dish_*");
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status,id);
        clearRedis("dish_*");
        return Result.success();
    }

    public void clearRedis(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
