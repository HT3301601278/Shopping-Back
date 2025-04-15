package example.shopping.controller;

import example.shopping.dto.AddressDTO;
import example.shopping.entity.Address;
import example.shopping.entity.User;
import example.shopping.service.AddressService;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/api/addresses")
@PreAuthorize("hasRole('USER')")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户的所有地址
     *
     * @return 地址列表
     */
    @GetMapping
    public Result<List<Address>> getAllAddresses() {
        Long userId = getCurrentUserId();
        return Result.success(addressService.findByUserId(userId));
    }

    /**
     * 获取地址详情
     *
     * @param id 地址ID
     * @return 地址信息
     */
    @GetMapping("/{id}")
    public Result<Address> getAddressById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        Address address = addressService.findById(id);

        // 验证地址是否属于当前用户
        if (address == null || !address.getUserId().equals(userId)) {
            return Result.error("地址不存在");
        }

        return Result.success(address);
    }

    /**
     * 获取默认地址
     *
     * @return 默认地址
     */
    @GetMapping("/default")
    public Result<Address> getDefaultAddress() {
        Long userId = getCurrentUserId();
        return Result.success(addressService.getDefault(userId));
    }

    /**
     * 添加地址
     *
     * @param addressDTO 地址信息
     * @return 添加的地址
     */
    @PostMapping
    public Result<Address> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        Long userId = getCurrentUserId();
        return Result.success(addressService.add(userId, addressDTO), "添加地址成功");
    }

    /**
     * 更新地址
     *
     * @param id         地址ID
     * @param addressDTO 地址信息
     * @return 更新后的地址
     */
    @PutMapping("/{id}")
    public Result<Address> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = getCurrentUserId();
        return Result.success(addressService.update(userId, id, addressDTO), "更新地址成功");
    }

    /**
     * 删除地址
     *
     * @param id 地址ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return Result.success(addressService.delete(userId, id), "删除地址成功");
    }

    /**
     * 设置默认地址
     *
     * @param id 地址ID
     * @return 是否设置成功
     */
    @PutMapping("/{id}/default")
    public Result<Boolean> setDefaultAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return Result.success(addressService.setDefault(userId, id), "设置默认地址成功");
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Address> getAddressByIdAdmin(@PathVariable Long id) {
        Address address = addressService.findById(id);
        if (address == null) {
            return Result.error("地址不存在");
        }
        return Result.success(address);
    }
}
