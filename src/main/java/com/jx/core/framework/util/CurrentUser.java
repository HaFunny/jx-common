package com.jx.core.framework.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Type: CurrentUser.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 16:14
 * @Description: 当前登录用户实体
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUser implements Serializable {
    private static final long serialVersionUID = 158719329006528066L;
    private String userAccountId; //用户账号id
    private String userName;
    private String userType;
    //TODO 待完善用户属性
}
