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

package com.eoi.jax.web.common;

public class ResponseResult<T> {
    private String retCode;

    private String retMsg;

    private T entity;

    private Long total;

    private String stackTrace;

    public ResponseResult() {
        this.retCode = ResponseCode.SUCCESS.code;
        this.retMsg = ResponseCode.SUCCESS.message;
    }

    public ResponseResult(ResponseCode msgCode) {
        this(msgCode.code,msgCode.message,null,null);
    }

    public ResponseResult(String retCode, String retMsg, T entity, Long total) {
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.entity = entity;
        this.total = total;
    }

    public static ResponseResult<Void> success() {
        return new ResponseResult<>(ResponseCode.SUCCESS.code, ResponseCode.SUCCESS.message,null,null);
    }

    public static <T> ResponseResult<T> success(T entity) {
        return new ResponseResult<>(ResponseCode.SUCCESS.code, ResponseCode.SUCCESS.message,entity,null);
    }

    public static <T> ResponseResult<T> success(T entity, Long total) {
        return new ResponseResult<>(ResponseCode.SUCCESS.code, ResponseCode.SUCCESS.message,entity,total);
    }

    public static ResponseResult<Void> of(ResponseCode msgCode) {
        return of(msgCode,null,null);
    }

    public static <T> ResponseResult<T> of(ResponseCode msgCode, T entity) {
        return of(msgCode,entity,null);
    }

    public static <T> ResponseResult<T> of(ResponseCode msgCode, T entity, Long total) {
        return new ResponseResult<>(msgCode.code,msgCode.message,entity,total);
    }

    public static ResponseResult<Void> of(String code, String msg) {
        return of(code,msg,null,null);
    }

    public static <T> ResponseResult<T> of(String code, String msg, T entity) {
        return of(code,msg,entity,null);
    }

    public static <T> ResponseResult<T> of(String code, String msg, T entity, Long total) {
        return new ResponseResult<>(code,msg,entity,total);
    }


    public String getRetCode() {
        return retCode;
    }

    public ResponseResult<T> setRetCode(String retCode) {
        this.retCode = retCode;
        return this;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public ResponseResult<T> setRetMsg(String retMsg) {
        this.retMsg = retMsg;
        return this;
    }

    public T getEntity() {
        return entity;
    }

    public ResponseResult<T> setEntity(T entity) {
        this.entity = entity;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public ResponseResult<T> setTotal(Long total) {
        this.total = total;
        return this;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public ResponseResult<T> setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }
}
