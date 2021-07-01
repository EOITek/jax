<template>
  <div class="sharp-selector-next"
       tabindex="0"
       @click="adding = !adding">
    <i :class="filterIcon" style="margin-right: 8px;"></i>
    <div class="ss-tag" @click.stop v-for="(key, index) in Object.keys(value)" :key="index">
      <span class="mr-5" v-if="value[key].not">!</span>
      <el-dropdown trigger="click" placement="bottom-start">
        <span class="ss-tag-text">
          {{curOption(key).name}}
          <i class="el-icon-arrow-down" style="height: 10px;"></i>
        </span>
        <el-dropdown-menu slot="dropdown" class="checkable maxheight-list">
          <el-dropdown-item v-for="option of filterOptions"
                            :key="option.key"
                            :disabled="option.key in value"
                            class="dropdown-item"
                            @click.native="changeCondition(option, key)">{{option.name}}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown> :
      <component :item="value[key]"
                 style="margin-left: 4px;"
                 :size="size"
                 class="flex-1"
                 :options="curOption(key).options"
                 @call="call(key)"
                 :is="typeof curOption(key).type === 'string'
                        ? components[curOption(key).type]
                        : curOption(key).type" />
      <i class="el-icon-close pointer" @click="close(key)"></i>
    </div>
    <!-- 添加时显示 -->
    <el-select :size="size"
               ref="Select"
               v-if="adding"
               :value="selectValue"
               value-key="key"
               filterable
               style="margin: 3px 0; height: 24px;"
               :filter-method="filterMethod"
               @change="add">
      <el-option v-for="option in filterOptions"
                 :key="option.key"
                 :disabled="option.key in value"
                 :value="option"
                 :label="option.name" />
    </el-select>
    <i class="el-icon-error clear" @click.stop="clear"></i>
  </div>
</template>
<script src="./sharp-selector.ts"></script>
<style src="./sharp-selector.scss" lang="scss" scoped></style>
