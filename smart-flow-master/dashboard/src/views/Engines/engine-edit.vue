<template>

  <div class="box">
    <div class="engine_edit_form">
      <div class="layui-form-item" hidden>
          <input type="text" name="title" v-model="engineInfo.id" autocomplete="off" class="layui-input" hidden>
      </div>
      <div class="layui-form-item">
        <label class="layui-form-label">引擎名称<span class="layui-required layui-icon">*</span></label>
        <div class="layui-input-block" style="width: 30%">
          <input type="text" name="title" v-model="engineInfo.engineName" required lay-verify="required" placeholder="请输入名称"
                 autocomplete="off" class="layui-input">
        </div>
      </div>
      <div class="layui-form-item layui-form-text">
        <label class="layui-form-label">编排内容<span class="layui-required layui-icon">*</span></label>
        <div class="layui-input-block layout-edit-line">
          <div class="layout-editor" id="editor" ref="editor"/>
          <div class="layout-preview engine_img_view">
            <img class="layout-preview-image" src="./blank.jpg" id="img_view"/>
          </div>
        </div>
      </div>
      <div class="layui-form-item">
        <div class="layui-input-block">
          <button class="layui-btn" v-on:click="validate()" click="javascript:void(0)">验证</button>
          <button class="layui-btn" v-on:click="save()">保存</button>
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

.engine_img_view {
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

.layout-preview-image {
  width: 100%;
}

.layout-preview {
  /* flex: 0 0 50%; */
  flex: 1 1 400px;
  overflow: scroll;
  height: 580px;
  margin-left: 12px;
}
</style>

<script>

import {onMounted, ref} from "vue";
import {useRouter} from 'vue-router';
import {engine_validate, engine_detail, engine_plant_uml_view, engine_save} from "../../api/module/api";
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
        value: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<engines xmlns=\"http://org.smartboot/smart-flow\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://org.smartboot/smart-flow\n" +
            "                             http://org.smartboot/smart-flow-1.0.1.xsd\">\n\n\n\n\n\n" +
            "</engines>", // 默认显示的值
        language: "xml",
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
    const router = useRouter();
    let id = -1;
    let url = window.location.href;

    if (url.split("/").length > 1) {
      let all = url.split('/');
      if (all.length > 0 && !isNaN(parseFloat(all[all.length - 1]))) {
        id = all[all.length - 1];
      }
    }

    const engineInfo = ref({id: id, engineName: ""});


    onMounted(() => {
      if (id > -1) {
        const func = async () => {
          const {data} = await engine_detail(id);
          engineInfo.value.engineName = data.engineName;
          monacoEditor.setValue(data.content);
        };
        func();
      }
    })


    return {
      engineInfo,
      validate: function () {
        const content = monacoEditor.getValue();
        const func = async () => {
          const data = await engine_validate(content);
          if (!data.success) {
            return;
          }

          engineInfo.value.engineName = data.data;

          // 生成plantuml
          const data2 = await engine_plant_uml_view(content);

          // 将plantuml容器填充内容
          //let blob = new Blob(data2,{type: "image/png"});
          //let url = window.URL.createObjectURL(blob);
          let url = 'data:image/png;base64,' + window.btoa(String.fromCharCode(...new Uint8Array(data2.data)));
          document.getElementById("img_view").src = url;
        };
        func();


      },

      save() {
        const content = monacoEditor.getValue();
        const save_engine = {id: engineInfo.value.id, engineName: engineInfo.value.engineName, content: content};
        const func = async () => {
          const data = await engine_save(save_engine);
          if (data.success) {
            router.push({path: "/engines"})
          }
        };
        func();
      }
    }


  }
}
</script>