package com.example.fishlsb.mapper;

import com.example.fishlsb.dto.DeviceInitResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TwinEntityMapper {
    List<DeviceInitResponse> findInitInfoByDeviceIds(@Param("deviceIds") List<String> deviceIds);
}