package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.basic.BasicListUI;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    public void addDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);
        //TODO 向表里插入数据后获取生成的主键，赋给dish中的id,取得这个id赋值给dishFlavor中的dishID
        Long dishId = dish.getId();

        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (dishFlavorList != null && dishFlavorList.size() > 0) {
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.addDishFlavor(dishFlavorList);
        }

    }

    @Override
    public PageResult showByPages(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = (Page<DishVO>) dishMapper.showByPages(dishPageQueryDTO);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) throws Exception {
        //判断菜品是否启售
        List<DishVO> list = dishMapper.selectByIds(ids);
        log.info("获取到的list：{}",list);
        if (list != null && list.size() != 0) {
            for (DishVO dvo : list) {
                if (dvo.getStatus() == 1) {
                    throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
            }
        }
        //判断菜品是否在关联套餐列表里
        Integer num=0;
        for(Long id: ids){
            num = setmealMapper.countByCategoryId(id);
        }
        log.info("获取到在setmeal里的数据有：{}",num);
        if (num!=0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //如果都通过了，则进行批量删除菜品；
        log.info("开始删除菜品");
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByIds(ids);

    }

    @Override
    public DishVO selectById(Long id) {
        DishVO dishVO = dishMapper.selectById(id);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.selectByDishId(id);
        dishVO.setFlavors(dishFlavorList);
        log.info("回显对象：{}",dishVO);
        return dishVO;
    }

    @Override
    @Transactional
    public void updateDishById(DishDTO dishDTO) {
        //将修改后的数据装入dish里
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        log.info("查看dish：{}",dish);

        //操作菜品表
        dishMapper.updateById(dish);


        //操作口味表
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();

        //为口味关联上dishid
        for(DishFlavor df:dishFlavorList){
            df.setDishId(dish.getId());
        }
        log.info("查看DTO信息：{}",dishDTO);

        if(dishFlavorList!=null&&dishFlavorList.size()!=0) {
            dishFlavorMapper.deleteById(dish.getId());
            dishFlavorMapper.addDishFlavor(dishFlavorList);
        }else{
            dishFlavorMapper.deleteById(dish.getId());
        }
        log.info("操作完成");

    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateById(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
