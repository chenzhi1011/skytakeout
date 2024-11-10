package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    @AutoFill(value = OperationType.INSERT)
    void addDish(Dish dish);

    List<DishVO> showByPages(DishPageQueryDTO dishPageQueryDTO);

    List<DishVO> selectByIds(List<Long> ids);

    void deleteByIds(List<Long> ids);

    DishVO selectById(Long id);

    /**
     * 动态条件查询菜品
     *
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    @AutoFill(OperationType.UPDATE)
    void updateById(Dish dish);


}
