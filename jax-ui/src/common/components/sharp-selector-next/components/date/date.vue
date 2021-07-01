<template>
  <el-popover placement="bottom-start"
              trigger="manual"
              v-model="visible">
    <div>
      <p class="mb-15">选择时间：</p>
      <el-date-picker v-model="item.value.timestamp"
                      class="mb-15"
                      ref="Date"
                      :editable="false"
                      value-format="timestamp"
                      type="datetime"
                      placeholder="选择日期时间" />
      <el-radio-group ref="radio" style="display: block;" v-model="item.value.range">
        <el-radio v-for="option in options"
                  :key="option.value"
                  :label="option.value">{{option.label}}</el-radio>
      </el-radio-group>
      <p class="text-right mt-15 pt-15" style="border-top: 1px solid #ccc;">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </p>
    </div>
    <input slot="reference"
           v-model="displayText"
           readonly
           ref="Input"
           class="input ml-3"
           style="width: 140px;"
           :class="{ edit: visible }"
           @focus="visible = true;"
           placeholder="点击选择" />
  </el-popover>
</template>
<script src="./date.ts"></script>
<style src="../input-style.scss" lang="scss" scoped></style>
