<template>

  <div v-if="reportInfo.visible" style="display: flex; flex-direction: column; align-items: center; align-content: center">
    <h2 style="display: flex; align-items: center; align-content: center">
        <i class="layui-icon-success layui-icon" style="color: green;"></i>Data uploaded to the successfully,
        <router-link v-if="reportInfo.type === '1'" :to="{path: '/report-manager/metrics-detail/' + reportInfo.id}">
          <span style="color: blue;">查看</span>
        </router-link>

        <router-link v-if="reportInfo.type === '2'" :to="{path: '/report-manager/trace-detail/' + reportInfo.id}">
          <span style="color: blue;">查看</span>
        </router-link>
    </h2>
    <lay-line/>
  </div>

  <div class="box">
    <div class="engine_edit_form">
      <div class="layui-form-item">
        <label class="layui-form-label">数据类型<span class="layui-required layui-icon">*</span></label>
        <div class="layui-input-block line-block" style="display: flex; align-items: center;">
          <input type="radio" id="id1" name="subtype" value="1" v-model="reportInfo.type"/><label for="id1">统计数据</label> &nbsp;&nbsp;&nbsp;
          <input type="radio" id="id2" name="subtype" value="2" v-model="reportInfo.type"/><label for="id2">链路数据</label>
        </div>
      </div>
      <div class="layui-form-item layui-form-text">
        <label class="layui-form-label">上报内容<span class="layui-required layui-icon">*</span></label>
        <div class="layui-input-block layout-edit-line">
          <div class="layout-editor" id="editor" ref="editor"/>
        </div>
      </div>
      <div class="layui-form-item">
        <div class="layui-input-block">
          <button class="layui-btn" v-on:click="save()">上报</button>
        </div>
      </div>
    </div>
  </div>

</template>

<style>
.box {
  display: flex;
  flex-direction: row;
  margin-top: 10px;
  margin-left: 10px;
}

.engine_edit_form {
  width: 100%;
  height: 90%;
}

.layout-edit-line {
  display: flex;
  height: 580px;
  flex-direction: row;
}

.layout-editor {
  flex: 1 1 400px;
  /* flex: 0 0 400px; */
}

</style>

<script>

import {ref} from "vue";
import {report_metrics, report_trace} from "../../api/module/api";
import * as monaco from "monaco-editor";

var monacoEditor;

export default {

  mounted() {
    this.init();
  },
  methods: {
    init() {
      // 使用 - 创建 monacoEditor 对象
      monacoEditor = monaco.editor.create(this.$refs.editor, {
        theme: "vs-dark", // 主题
        value: "", // 默认显示的值
        language: "json",
        folding: true, // 是否折叠
        foldingHighlight: true, // 折叠等高线
        foldingStrategy: "indentation", // 折叠方式  auto | indentation
        showFoldingControls: "always", // 是否一直显示折叠 always | mouseover
        disableLayerHinting: true, // 等宽优化
        emptySelectionClipboard: false, // 空选择剪切板
        selectionClipboard: false, // 选择剪切板
        automaticLayout: true, // 自动布局
        codeLens: false, // 代码镜头
        suggestFontSize: 14,
        fontSize: 14,
        scrollBeyondLastLine: false, // 滚动完最后一行后再滚动一屏幕
        colorDecorators: true, // 颜色装饰器
        accessibilitySupport: "auto", // 辅助功能支持  "auto" | "off" | "on"
        lineNumbers: "on", // 行号 取值： "on" | "off" | "relative" | "interval" | function
        lineNumbersMinChars: 5, // 行号最小字符   number
        readOnly: false, //是否只读  取值 true | false
      });
    },
  },

  setup() {
    const reportInfo = ref({type : '1', visible: false, id: -1})

    return {
      reportInfo,
      save() {
        const content = monacoEditor.getValue();
        const saveAsync = async() => {
          if (reportInfo.value.type === '1') {
            const data = await report_metrics(content);
            if (data && data.data) {
              reportInfo.value.id = data.data;
              reportInfo.value.visible = true;
            }
          } else if (reportInfo.value.type === '2') {
            const data = await report_trace(content);
            if (data && data.data) {
              reportInfo.value.id = data.data;
              reportInfo.value.visible = true;
            }
          }
        }


        saveAsync();
      }
    }


  }
}
</script>