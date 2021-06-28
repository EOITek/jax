/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.web.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_user")
public class TbUser {

    @TableId("user_account")
    private String userAccount;

    @TableField("user_password")
    private String userPassword;

    @TableField("user_name")
    private String userName;

    @TableField("user_email")
    private String userEmail;

    @TableField("user_phone")
    private String userPhone;

    @TableField("user_disabled")
    private Integer userDisabled;

    @TableField("login_time")
    private Long loginTime;

    @TableField("session_token")
    private String sessionToken;

    @TableField("session_expire")
    private Long sessionExpire;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    public String getUserAccount() {
        return userAccount;
    }

    public TbUser setUserAccount(String userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public TbUser setUserPassword(String userPassword) {
        this.userPassword = userPassword;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public TbUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public TbUser setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public TbUser setUserPhone(String userPhone) {
        this.userPhone = userPhone;
        return this;
    }

    public Integer getUserDisabled() {
        return userDisabled;
    }

    public TbUser setUserDisabled(Integer userDisabled) {
        this.userDisabled = userDisabled;
        return this;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public TbUser setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public TbUser setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    public Long getSessionExpire() {
        return sessionExpire;
    }

    public TbUser setSessionExpire(Long sessionExpire) {
        this.sessionExpire = sessionExpire;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbUser setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public TbUser setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public TbUser setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public TbUser setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }
}
