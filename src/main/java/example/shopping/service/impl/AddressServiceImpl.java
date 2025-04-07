package example.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import example.shopping.dto.AddressDTO;
import example.shopping.entity.Address;
import example.shopping.entity.User;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.AddressMapper;
import example.shopping.service.AddressService;
import example.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserService userService;

    @Override
    public List<Address> findByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address findById(Long id) {
        return addressMapper.findById(id);
    }

    @Override
    @Transactional
    public Address add(Long userId, AddressDTO addressDTO) {
        // 如果是默认地址，则将用户其他地址设为非默认
        if (addressDTO.getIsDefault()) {
            resetDefaultAddress(userId);
        }
        
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());
        address.setIsDefault(addressDTO.getIsDefault());
        
        Date now = new Date();
        address.setCreateTime(now);
        address.setUpdateTime(now);
        
        addressMapper.insert(address);

        // 同步更新用户表中的addresses字段
        syncUserAddresses(userId);
        
        return address;
    }

    @Override
    @Transactional
    public Address update(Long userId, Long id, AddressDTO addressDTO) {
        Address address = addressMapper.findById(id);
        
        // 验证地址是否存在且属于当前用户
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        
        // 如果设为默认地址，则将用户其他地址设为非默认
        if (addressDTO.getIsDefault() && !address.getIsDefault()) {
            resetDefaultAddress(userId);
        }
        
        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());
        address.setIsDefault(addressDTO.getIsDefault());
        address.setUpdateTime(new Date());
        
        addressMapper.update(address);

        // 同步更新用户表中的addresses字段
        syncUserAddresses(userId);
        
        return address;
    }

    @Override
    @Transactional
    public boolean delete(Long userId, Long id) {
        Address address = addressMapper.findById(id);
        
        // 验证地址是否存在且属于当前用户
        if (address == null) {
            return false;
        }
        
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        
        boolean result = addressMapper.deleteById(id) > 0;
        if (result) {
            // 同步更新用户表中的addresses字段
            syncUserAddresses(userId);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean setDefault(Long userId, Long id) {
        // 先查询地址是否存在且属于当前用户
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        
        // 如果已经是默认地址，则不需要操作
        if (address.getIsDefault()) {
            return true;
        }
        
        // 先将所有地址设为非默认
        resetDefaultAddress(userId);
        
        // 再将当前地址设为默认
        address.setIsDefault(true);
        address.setUpdateTime(new Date());
        
        boolean result = addressMapper.update(address) > 0;
        if (result) {
            // 同步更新用户表中的addresses字段
            syncUserAddresses(userId);
        }
        return result;
    }

    @Override
    public Address getDefault(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }
    
    /**
     * 重置用户所有地址为非默认
     * @param userId 用户ID
     */
    private void resetDefaultAddress(Long userId) {
        addressMapper.resetDefault(userId);
    }

    /**
     * 同步更新用户表中的addresses字段
     * @param userId 用户ID
     */
    private void syncUserAddresses(Long userId) {
        // 获取用户所有地址
        List<Address> addresses = addressMapper.findByUserId(userId);
        
        List<Long> addressIds = addresses.stream()
                .map(Address::getId)
                .collect(java.util.stream.Collectors.toList());

        // 更新用户表
        User user = userService.findById(userId);
        if (user != null) {
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setAddresses(JSON.toJSONString(addressIds));
            updateUser.setUpdateTime(new Date());
            userService.updateUser(updateUser);
        }
    }
} 