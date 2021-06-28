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

package com.eoi.jax.web.common.exception;

import com.eoi.jax.web.common.ResponseCode;

public class BizException extends RuntimeException {
    private final String code;
    private final String message;
    private Throwable cause;
    private Object entity;

    public BizException(ResponseCode code) {
        this(code.code, code.message);
    }

    public BizException(ResponseCode code, String jobId) {
        this(code.code, code.message,jobId);
    }

    public BizException(ResponseCode code, Throwable cause) {
        this(code.code, code.message, cause);
    }

    public BizException(ResponseCode code, Throwable cause, Object entity) {
        this(code.code, code.message, cause,entity);
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(String code, String message, Object entity) {
        super(message);
        this.code = code;
        this.message = message;
        this.entity = entity;
    }

    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    public BizException(String code, String message, Throwable cause, Object entity) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.cause = cause;
        this.entity = entity;
    }

    public String getCode() {
        return code;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}

