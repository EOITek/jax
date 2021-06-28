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

package com.eoi.jax.web.common.annotation;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.ResponseResult;
import com.eoi.jax.web.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

@ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private HttpServletRequest request;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult exceptionHandler(Exception exception) {
        logger.debug("trace error, uri:{}", request.getRequestURI(), exception);
        if (exception instanceof MethodArgumentTypeMismatchException) {
            throw (MethodArgumentTypeMismatchException) exception;
        } else if (exception instanceof BizException) {
            BizException ex = (BizException) exception;
            String stackTrace = stackTrace(ex);
            ResponseResult responseResult = new ResponseResult().setRetCode(ex.getCode()).setRetMsg(ex.getMessage()).setStackTrace(stackTrace);
            Optional.ofNullable(ex.getEntity()).ifPresent(responseResult::setEntity);
            return responseResult;
        } else if (exception instanceof DataAccessException) {
            logger.error("数据库异常,uri:{}", request.getRequestURI(), exception);
            return new ResponseResult()
                    .setRetCode(ResponseCode.DB_FAILED.code)
                    .setRetMsg(ResponseCode.DB_FAILED.message)
                    .setStackTrace(stackTrace(exception));
        } else {
            logger.error("内部异常,uri:{}", request.getRequestURI(), exception);
            return new ResponseResult()
                    .setRetCode(ResponseCode.UNDEFINED.code)
                    .setRetMsg(ResponseCode.UNDEFINED.message)
                    .setStackTrace(stackTrace(exception));
        }
    }

    private String stackTrace(Throwable ex) {
        if (ex == null) {
            return null;
        }
        return ExceptionUtil.stacktraceToString(ex, 10240);
    }
}
