package example.shopping.service.impl;

import example.shopping.dto.AddressDTO;
import example.shopping.entity.Address;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.AddressMapper;
import example.shopping.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

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
        
        return addressMapper.deleteById(id) > 0;
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
        
        return addressMapper.update(address) > 0;
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
} 