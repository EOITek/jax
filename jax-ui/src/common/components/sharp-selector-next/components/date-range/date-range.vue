<template>
  <el-date-picker v-model="item.value"
                  v-if="editing"
                  ref="Date"
                  :size="size"
                  :editable="false"
                  value-format="timestamp"
                  @change="change"
                  @blur="blur"
                  style="height: 20px; line-height: 20px;"
                  type="datetimerange"
                  placeholder="选择日期时间" />
  <div class="pointer"
       @click="edit"
       v-else-if="item.value && item.value.length">
    {{item.value[0] | formatDate}} - {{item.value[1] | formatDate}}
  </div>
  <div class="pointer" @click="edit" v-else>--</div>
</template>
<script src="./date-range.ts"></script>
<style lang="scss" scoped>
.el-date-editor {
  ::v-deep {
    .el-input__icon,
    .el-range-separator {
      line-height: 13px;
    }
  }
}
</style>
