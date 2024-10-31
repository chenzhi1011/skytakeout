package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface DishFlavorMapper {
    void deleteByIds(List<Long> ids);

    List<DishFlavor> selectByDishId(Long id);

    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteById(Long id);

    void addDishFlavor(List<DishFlavor> dishFlavorList);
}
