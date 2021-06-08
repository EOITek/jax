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

package com.eoi.jax.core;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.BatchProcessBuilder;
import com.eoi.jax.api.BatchSinkBuilder;
import com.eoi.jax.api.BatchSourceBuilder;
import com.eoi.jax.api.BatchSqlBuilder;
import com.eoi.jax.api.StreamingDebugSinker;
import com.eoi.jax.api.StreamingProcessBuilder;
import com.eoi.jax.api.StreamingSinkBuilder;
import com.eoi.jax.api.StreamingSourceBuilder;
import com.eoi.jax.api.StreamingSqlBuilder;
import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.annotation.model.JobParamMeta;
import com.eoi.jax.api.tuple.Tuple;
import com.eoi.jax.api.tuple.Tuple3;
import com.eoi.jax.common.ReflectUtil;
import com.eoi.jax.core.flink.python.PythonWrapperJob;
import com.eoi.jax.core.flink.python.PythonWrapperJobConfig;
import com.eoi.jax.core.spark.python.BridgeJob;
import com.eoi.jax.core.spark.python.BridgeJobConfig;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JobMetaScanner 用于反射获取Job的注解信息，泛型参数信息，配置信息
 */
public class JobMetaScanner {


    // Utility classes should not have public constructors
    private JobMetaScanner() {
    }

    public static JobMeta getJobMetaByPythonJobInfo(PythonJobInfo pythonJobInfo) throws JobMetaScanException, JobMetaException {
        JobMeta jobMeta = new JobMeta();
        Class bridgeClass = null;
        List<JobParamMeta> jobParamMetas = new ArrayList<>();
        if (JobInfo.TYPE_STREAMING.equals(pythonJobInfo.getType())) {
            bridgeClass = PythonWrapperJob.class;
            jobParamMetas = com.eoi.jax.api.reflect.ParamUtil.scanJobParams(PythonWrapperJobConfig.class);
        } else if (JobInfo.TYPE_BATCH.equals(pythonJobInfo.getType())) {
            bridgeClass = BridgeJob.class;
            jobParamMetas = com.eoi.jax.api.reflect.ParamUtil.scanJobParams(BridgeJobConfig.class);
        }
        Tuple3<Type, Type, Type> t3 = getInOrOutOrCType(bridgeClass);
        jobMeta.inTypes = getTypeDescriptors(t3.f0);
        jobMeta.outTypes = getTypeDescriptors(t3.f1);
        JobInfo jobInfo = new JobInfo();
        jobInfo.setApiVersion(2);
        jobInfo.setName(pythonJobInfo.getAlgClassName());
        jobInfo.setType(pythonJobInfo.getType());
        jobInfo.setRole(JobInfo.ROLE_PROCESS);
        jobInfo.setDisplay(pythonJobInfo.getDisplay());
        jobInfo.setDescription(pythonJobInfo.getDescription());
        jobMeta.jobInfo = jobInfo;

        for (PythonJobInfo.PythonParameterInfo paramInfo : pythonJobInfo.getParameters()) {
            JobParamMeta jobParamMeta = new JobParamMeta();
            jobParamMeta.setName(paramInfo.getName());
            jobParamMeta.setLabel(paramInfo.getLabel());
            jobParamMeta.setDescription(paramInfo.getDescription());
            if (StrUtil.isNotBlank(paramInfo.getType())) {
                jobParamMeta.setType(new DataType[] {parseDataType(paramInfo.getType())});
            }
            if (StrUtil.isNotBlank(paramInfo.getOptional())) {
                jobParamMeta.setOptional(Boolean.parseBoolean(paramInfo.getOptional()));
            }
            jobParamMeta.setDescription(paramInfo.getDescription());
            jobParamMeta.setDefaultValue(paramInfo.getDefaultValue());
            jobParamMetas.add(jobParamMeta);
        }

        jobMeta.jobParameters = jobParamMetas;

        return jobMeta;
    }

    private static DataType parseDataType(String type) throws JobMetaException {
        return Arrays.stream(DataType.primitives)
                .filter(p -> p.toString().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() ->
                        new JobMetaException("the parameter type of python job only support primitive type"));
    }

    /**
     * 获取某个JobBuilder类的JobMeta
     *
     * @param targetClass 目标类型
     * @return JobMeta
     * @throws JobMetaScanException 如果有异常会抛出
     */
    public static JobMeta getJobMetaByClass(Class targetClass) throws JobMetaScanException {
        if (targetClass == null) {
            throw new IllegalArgumentException();
        }
        JobMeta jobMeta = new JobMeta();
        Tuple3<Type, Type, Type> t3 = getInOrOutOrCType(targetClass);

        Job ann2 = (Job) targetClass.getAnnotation(Job.class);
        if (ann2 != null) {
            // V2 版本
            jobMeta.jobInfo = getJobInfo(ann2, targetClass);
            // 处理Config参数
            jobMeta.jobParameters = com.eoi.jax.api.reflect.ParamUtil.scanJobParams((Class) t3.f2);
        }

        // 处理IN
        jobMeta.inTypes = getTypeDescriptors(t3.f0);
        // 处理OUT
        jobMeta.outTypes = getTypeDescriptors(t3.f1);

        return jobMeta;
    }

    public static JobInfo getJobInfo(Job annotation) {
        if (annotation == null) {
            return null;
        }
        JobInfo info = new JobInfo();
        info.setApiVersion(1);
        info.setName(annotation.name());
        info.setDisplay(annotation.display());
        info.setDescription(annotation.description());
        return info;
    }

    public static JobInfo getJobInfo(Job annotation, Class targetClass) {
        if (annotation == null) {
            return null;
        }
        JobInfo info = new JobInfo();
        info.setApiVersion(2);
        info.setName(annotation.name());
        info.setDisplay(annotation.display());
        info.setDescription(annotation.description());
        info.setCategory(annotation.category());
        info.setDoc(annotation.doc());
        info.setIcon(annotation.icon());
        info.setInternal(annotation.isInternal());
        if (StreamingSourceBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_STREAMING);
            info.setRole(JobInfo.ROLE_SOURCE);
        } else if (StreamingProcessBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_STREAMING);
            info.setRole(JobInfo.ROLE_PROCESS);
        } else if (StreamingSinkBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_STREAMING);
            info.setRole(JobInfo.ROLE_SINK);
        } else if (BatchSourceBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_BATCH);
            info.setRole(JobInfo.ROLE_SOURCE);
        } else if (BatchProcessBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_BATCH);
            info.setRole(JobInfo.ROLE_PROCESS);
        } else if (BatchSinkBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_BATCH);
            info.setRole(JobInfo.ROLE_SINK);
        } else if (StreamingDebugSinker.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_STREAMING);
            info.setRole(JobInfo.ROLE_PROCESS);
            info.setInternal(true);
        } else if (StreamingSqlBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_STREAMING);
            info.setRole(JobInfo.ROLE_PROCESS);
            info.setInternal(true);
        } else if (BatchSqlBuilder.class.isAssignableFrom(targetClass)) {
            info.setType(JobInfo.TYPE_BATCH);
            info.setRole(JobInfo.ROLE_PROCESS);
            info.setInternal(true);
        }
        return info;
    }

    public static List<JobMeta.InOutTypeDescriptor> getTypeDescriptors(Type type) throws JobMetaScanException {
        if (type == null) {
            return null;
        }
        List<JobMeta.InOutTypeDescriptor> list = new ArrayList<>();
        if (ReflectUtil.isAssignableBetween(Tuple.class, type)) {
            // 处理Tuple的情况
            if (type instanceof ParameterizedType) {
                Type[] requiredTypes = ((ParameterizedType) type).getActualTypeArguments();
                // 对Tuple里面的每个Type
                for (int i = 0; i < requiredTypes.length; i++) {
                    list.add(getTypeDescriptor(requiredTypes[i]));
                }
            } else {
                // Tuple必须是泛型
                throw new JobMetaScanException("`Tuple` must be ParameterizedType");
            }
        } else {
            list.add(getTypeDescriptor(type));
        }
        return list;
    }

    public static JobMeta.InOutTypeDescriptor getTypeDescriptor(Type type) {
        JobMeta.InOutTypeDescriptor inOutTypeDescriptor = new JobMeta.InOutTypeDescriptor();
        if (type instanceof ParameterizedType) {
            // 处理泛型
            ParameterizedType pt = (ParameterizedType) type;
            inOutTypeDescriptor.raw = pt.getRawType().getTypeName();
            inOutTypeDescriptor.parameterizedTypes = new ArrayList<>();
            Type[] subTypes = pt.getActualTypeArguments();
            for (int i = 0; i < subTypes.length; i++) {
                inOutTypeDescriptor.parameterizedTypes.add(subTypes[i].getTypeName());
            }
        } else {
            // 处理普通类型
            inOutTypeDescriptor.raw = type.getTypeName();
        }
        return inOutTypeDescriptor;
    }

    public static Tuple3<Type, Type, Type> getInOrOutOrCType(Class targetClass) throws JobMetaScanException {
        List<Class> targetSuperInterfaces = Arrays.asList(
                StreamingSourceBuilder.class,
                StreamingProcessBuilder.class,
                StreamingSinkBuilder.class,
                BatchSourceBuilder.class,
                BatchProcessBuilder.class,
                BatchSinkBuilder.class,
                StreamingDebugSinker.class,
                StreamingSqlBuilder.class,
                BatchSqlBuilder.class
        );
        for (Class tsi : targetSuperInterfaces) {
            Tuple3<Type, Type, Type> r = internalGetInOrOutOrCType(targetClass, tsi);
            if (r != null) {
                return r;
            }
        }
        String impls = targetSuperInterfaces.stream().map(Class::getCanonicalName).collect(Collectors.joining(","));
        throw new JobMetaScanException(targetClass + " is not implement one of " + impls);
    }

    private static Tuple3<Type, Type, Type> internalGetInOrOutOrCType(Class targetClass, Class targetSuperInterface) {
        Map<String, Type> sourceJobBuilderTyped = ReflectUtil.scanParameterizedType(targetClass, targetSuperInterface);
        if (sourceJobBuilderTyped == null) {
            return null;
        }
        Tuple3<Type, Type, Type> t3 = (Tuple3) Tuple.newInstance(3);
        Type inType = sourceJobBuilderTyped.get("IN");
        Type outType = sourceJobBuilderTyped.get("OUT");
        Type configType = sourceJobBuilderTyped.get("C");
        t3.setField(inType, 0);
        t3.setField(outType, 1);
        t3.setField(configType, 2);
        return t3;
    }

    public static class JobMetaScanException extends Exception {

        public JobMetaScanException() {
        }

        public JobMetaScanException(String message) {
            super(message);
        }

        public JobMetaScanException(String message, Exception ex) {
            super(message, ex);
        }
    }
}
