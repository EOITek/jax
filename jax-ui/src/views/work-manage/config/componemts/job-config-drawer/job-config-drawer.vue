<template>
  <div class="flex-column ptb-10 config-box">
    <el-form ref="Form"
             class="plr-10"
             :model="jobUiData"
             label-width="80px"
             label-position="top"
             :disabled="isDebugModel">
      <el-form-item>
        <template #label>
          <div class="flex-vcenter">
            <span>节点名称</span>
            <i class="el-icon-share ml-10 pointer" @click="handleJobShare"></i>
          </div>
        </template>
        <el-input v-model="jobUiData.display" ref="Display" />
      </el-form-item>
      <el-form-item label="节点描述">
        <el-input v-model="jobUiData.description" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" />
      </el-form-item>
      <el-form-item class="mb-0">
        <template #label>
          <el-button type="text" class="fs-14" @click="handlerOpenConfigHelpModal" :disabled="!jobUiData.hasDoc">
            配置帮助
            <i class="el-icon-paperclip ml-5"></i>
          </el-button>
        </template>
      </el-form-item>
    </el-form>
    <el-tabs value="config">
      <el-tab-pane label="节点参数" name="config">
        <el-form :model="jobParametersFormData" ref="jobParametersForm" label-position="top" :disabled="isDebugModel">
          <job-parameter v-for="parameter in viewParameters"
                         :key="parameter.prop"
                         :formProp="parameter.formProp"
                         :isDebugModel="isDebugModel"
                         :parameter="parameter" />
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script lang="ts" src="./job-config-drawer.ts"></script>
<style lang="scss" scoped>
::v-deep .el-tabs__content {
  overflow: auto;
}

::v-deep .el-tabs__nav-wrap {
  padding: 0 10px 0;
}

::v-deep .el-tabs__header {
  margin: 0;
}

.config-box {
  overflow: auto;
  background: #fafafa;
}
</style>
