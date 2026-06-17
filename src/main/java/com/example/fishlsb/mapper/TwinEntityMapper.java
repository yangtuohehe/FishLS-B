package com.example.fishlsb.mapper;

import com.example.fishlsb.entity.TwinEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TwinEntityMapper {
    List<TwinEntity> findByDeviceIds(List<String> deviceIds);
}