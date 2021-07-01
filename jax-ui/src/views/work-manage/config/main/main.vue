<template>
  <div class="flex-column main-content" :style="{ height: `calc(100vh - ${debugDrawerHeight + 70}px)` }" >
    <div class="flex-column flex-1">
      <header class="flex-vcenter" style="justify-content: space-between;">
        <div class="flex-vcenter width-full">
          <router-link :to="{ name: 'work-manage/list', params: { pipelineType: jobType } }">
            <i class="el-icon-arrow-left fs-24 pointer"></i>
          </router-link>
          <input v-if="pipelineData"
                 v-model="pipelineData.pipelineName"
                 :disabled="!isCreate"
                 class="fs-24 font-bold mlr-15"
                 style="border: none; width: 230px;" />
          <template v-if="!isDebugModel">
            <el-button @click="save">
              <i class="fa fa-floppy-o mr-3"></i>
              保存
            </el-button>
            <el-button type="primary" @click="handlerRelease">
              <i class="fa fa-paper-plane-o mr-3"></i>
              发布
            </el-button>
            <el-button type="primary" @click="handlerOpenJsonModel">
              <i class="el-icon-document mr-3"></i>
              JSON
            </el-button>
            <el-button type="primary" @click="handlerOpenDebugDrawer">
              进入调试
            </el-button>
            <el-button type="primary" @click="handlerOpenAdvancedConfig">高级配置</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="handlerStartDebug" :disabled="isStartDebug">
              开始
            </el-button>
            <el-button type="danger" @click="handlerStopDebug('stop')" :disabled="!isStartDebug">
              停止
            </el-button>
            <el-button type="danger" @click="handlerStopDebug('close')">
              关闭调试
            </el-button>
          </template>
          <a class="ml-auto fs-18 fc-primary" v-if="webUrl" :href="webUrl" target="_break">追踪</a>
        </div>
      </header>
      <main class="flex-1 flex" style="min-height: 175px">
        <job-list :job-list="jobList" :job-type="jobType" v-show="!isDebugModel" ref="jobList" />
        <job-dagre class="flex-1"
                   v-if="pipelineData"
                   :pipeline-data="pipelineData"
                   :is-debug-model="isDebugModel"
                   :is-start-debug="isStartDebug"
                   :is-streaming="isStreaming"
                   @openJobConfigDrawer="handlerOpenJobConfigDrawer"
                   @deleteNode="showJobConfigDrawer = false"
                   ref="JobDagre" />
      </main>
    </div>
    <el-drawer :with-header="false"
               :visible.sync="isDebugModel"
               :size="`${debugDrawerHeight}px`"
               :destroy-on-close="true"
               v-if="isDebugModel"
               :resizable="true"
               max-size="calc(100vh - 300px)"
               min-size="180px"
               ref="ElDrawer"
               :modal="false"
               class="job-debug-drawer"
               direction="btt"
               @resize="handledrawerResize">
      <JobDebugDrawer ref='JobDebugDrawer'
                      :pipeline-data="pipelineData"
                      :is-streaming="isStreaming"
                      :web-url.sync="webUrl"
                      :is-start-debug="isStartDebug"
                      @stopDebug="handlerStopDebug('stop')"
                      @updateEdgeRunningState="handleUpdateEdgeRunningState" />
      <div slot="resizable" class="drag-handler drag-handler-h"></div>
    </el-drawer>
    <el-drawer :visible.sync="showJobConfigDrawer"
               :destroy-on-close="true"
               :resizable="true"
               :title="pipelineData && pipelineData.pipelineName"
               min-size="320px"
               size="320px"
               :modal="false"
               custom-class="job-config-drawer">
      <div class="flex-column height-full">
        <JobConfigDrawer class="flex-1"
                         ref="JobConfigDrawer"
                         v-if="showJobConfigDrawer"
                         :job-config="jobConfig"
                         :parameters="parameters"
                         :job-ui-data="pipelineData.pipelineUi[currentJobId]"
                         :is-debug-model="isDebugModel"
                         :job-name="currentJobName"
                         @updateJobShareList="$refs.jobList.fetchJobshareList" />
        <div class="footer">
          <el-button @click="showJobConfigDrawer = false">取消</el-button>
          <el-button type="primary" @click="handleJobConfigDrawerAffirm">确认</el-button>
        </div>
      </div>
      <div slot="resizable" class="drag-handler drag-handler-v"></div>
    </el-drawer>
  </div>

</template>
<script src="./main.tsx" />
<style lang="scss" scoped>
.main-content {
  padding: 0;
  position: relative;
  min-height: 229px;
}

header {
  background: #F5F9FE;
  height: 55px;
  border-bottom: 1px solid #E1E3E6;
  padding: 0 16px;
}

.job-debug-drawer ::v-deep {
  .el-drawer-drag-move-trigger {
    cursor: row-resize;
  }
}

.footer {
  background: white;
  border-top: solid 1px #E2E2E2;
  padding: 10px 20px 20px;
  text-align: right;
}

.drag-handler {
  border: solid #dbdbdb;
  user-select: none;
  box-sizing: content-box;
  border-width: 1px 0;
  --direction: 90deg;
  height: 2px;
  background: #f0f0f0 linear-gradient(var(--direction),#adadad 0,#adadad 2px,transparent 2px,transparent 4px,#adadad 4px,#adadad 6px,transparent 6px,transparent 8px,#adadad 8px,#adadad 10px) no-repeat center;
  background-size: 10px 2px;
}

.drag-handler.drag-handler-v {
  width: calc(100vh - 122px);
  cursor: col-resize;
}

.drag-handler.drag-handler-h {
  width: 100vw;
  cursor: row-resize;
}
</style>
