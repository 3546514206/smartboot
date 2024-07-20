import {createApp} from 'vue'
import Router from './router'
import Store from './store'
import App from './App.vue'
import {permission} from "./directives/permission";
import './mockjs'

const app = createApp(App)

app.use(Store);
app.use(Router);

app.directive("permission",permission);

app.mount('#app');

// 代码高亮插件
import hljs from 'highlight.js';
// 必须导入 否则没样式
import 'highlight.js/styles/github-dark-dimmed.css';
import 'highlight.js/lib/common';
import hjsVuePlugin from '@highlightjs/vue-plugin';
const high: any = {
    deep: true,
    bind: function (el: any, binding: any) {
        const targets = el.querySelectorAll('code')
        targets.forEach((target: any) => {
            if (binding.value) {
                target.textContent = binding.value;
            }
            (hljs as any).highlightBlock(target);
        })
    },
    componentUpdated: function (el: any, binding: any) {
        const targets = el.querySelectorAll('code')
        targets.forEach((target: any) => {
            if (binding.value) {
                target.textContent = binding.value;
                (hljs as any).highlightBlock(target);
            }
        })
    }
}
app.directive('highlightjs', high)
app.use(hjsVuePlugin);
