<template>
  <div v-if="displayText && displayText.length" class="flex flex-vcenter">
    <el-dropdown v-for="(option, depth) in displayText" :key="depth" trigger="click">
      <span class="pointer">{{option.label}}</span>
      <span v-if="depth < displayText.lastIndex" class="pr-5">.</span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item v-for="bortherItem in option.brother"
                          @click.native="changeValue(bortherItem, depth)"
                          :key="bortherItem.value">{{bortherItem.label}}</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <template v-if="displayText.lastItem.children">
      <el-select :value="undefined"
                 ref="subSelect"
                 :size="size"
                 class="ml-5"
                 value-key="value"
                 @visible-change="visibleChange"
                 @change="selectNext"
                 v-if="editing">
        <el-option v-for="child in displayText.lastItem.children"
                   :key="child.value"
                   :value="child"
                   :label="child.label" />
      </el-select>
      <span v-else class="ml-5 pointer" @click="editSubOpt">--</span>
    </template>
  </div>
  <el-select :size="size"
             ref="Select"
             v-else
             :value="undefined"
             @change="change">
    <el-option v-for="item in options"
               :key="item.value"
               :value="item.value"
               :label="item.label" />
  </el-select>
</template>
<script src="./cascader.ts"></script>
