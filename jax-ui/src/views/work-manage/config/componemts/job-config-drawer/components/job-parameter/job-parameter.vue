<template>
  <el-form-item :prop="prop"
                :rules="parameter.formRule"
                class="el-form-item"
                :class="[`bg-neutrals-${parameter.tier + 1}`, isComplexType && 'list-box' ]" >
    <div class="label flex-vcenter pr-10" :class="!isComplexType && 'pl-10'" slot="label"  v-if="parameterLabel">
      <i class="el-icon-caret-right pointer fs-18 mr-5"
         v-if="isComplexType"
         :class="isSpread && 'rotate-90'"
         @click="isSpread = !isSpread"></i>
      <p class="text-ellipsis" style="max-width: calc(100% - 40px)">{{ parameterLabel }}</p>
      <i class="el-icon-question primary-color mlr-2" v-if="isShowLabelInfo" v-tooltip="parameterDesc"></i>
      <span class="fc-red mr-10" v-if="!parameter.optional && isShowLabelInfo">*</span>
      <slot name="removeObject"/>
    </div>
    <div class="flex-vcenter width-full" :class="!isComplexType && 'plr-10'" style="position: relative">
      <input-string v-if="parameter.type[0] === 'STRING'"
                    :parameter="parameter"
                    class="flex-1"
                    :isDebugModel="isDebugModel" />

      <template v-if="isNumber">
        <el-select v-model="parameter.value"
                   placeholder="请选择"
                   class="flex-1"
                   v-if="parameter.candidates && parameter.candidates.length">
          <el-option v-for="item in parameter.candidates"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
        <el-input-number v-model="parameter.value"
                         :min="Number.MIN_SAFE_INTEGER"
                         :max="Number.MAX_SAFE_INTEGER"
                         @mousedown.native="!parameter.value && (parameter.value = 0)"
                         v-else />
      </template>

      <template v-if="parameter.type[0] === 'BOOL'">
        <el-radio-group v-model="parameter.value">
          <el-radio :label="null">默认</el-radio>
          <el-radio :label="true">开启</el-radio>
          <el-radio :label="false">关闭</el-radio>
        </el-radio-group>
      </template>

      <template v-if="isList">
        <div class="ml-10 pr-10 complex-type" v-show="isSpread">
          <job-parameter v-for="(listParameter,index) in parameter.viewDataList"
                         :key="`${prop}[${index}]${+new Date()}`"
                         :label="`${parameterLabel}[${index + 1}]`"
                         :isShowLabelInfo="false"
                         :prevProp="`${prop}[${index}]`"
                         :class="index === parameter.viewDataList.length - 1 && 'mb-0'"
                         :parameter="listParameter">
            <template v-slot:default="listParameterDefault">
              <i class="el-icon-delete pointer fc-red fs-18 ml-10"
                 v-if="!listParameterDefault.isComplexType"
                 @click="handlerSubListItem(index)">
              </i>
            </template>
            <template v-slot:removeObject>
              <i class="el-icon-delete pointer fc-red fs-18 ml-auto"
                 @click="handlerSubListItem(index)">
              </i>
            </template>
          </job-parameter>
          <i class="el-icon-circle-plus-outline pointer fc-primary fs-18 ml-10"
             @click="handlerAddListItem"
             v-if="parameter.listParameter">
          </i>
          <p class="fc-red" v-if="!parameter.listParameter">参数配置错误</p>
        </div>
      </template>

      <template v-if="isObject">
        <div class="complex-type pr-10 pb-10"
             :class="parameterLabel && 'ml-10'"
             v-show="!parameterLabel || isSpread">
          <job-parameter v-for="(objParameter,index) in parameter.objectParameters"
                         :key="objParameter.prevProp + objParameter.formProp"
                         :prevProp="prop"
                         :formProp="objParameter.formProp"
                         :class="index === parameter.objectParameters.length - 1 && 'mb-0'"
                         :parameter="objParameter" />
        </div>
      </template>

      <template v-if="isMap">
        <map-list :viewDataMap="parameter.viewDataMap" v-show="isSpread" />
      </template>
      <slot :isComplexType="isComplexType" />
    </div>
  </el-form-item>
</template>
<script lang="ts" src="./job-parameter.ts"></script>
<style lang="scss" src="./job-parameter.scss" scoped />
