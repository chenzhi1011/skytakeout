package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDish(DishDTO dishDTO);

    PageResult showByPages(DishPageQueryDTO dishPageQueryDTO);

    void deleteById(List<Long> ids) throws Exception;

    DishVO selectById(Long id);

    void updateDishById(DishDTO dishDTO);
}
