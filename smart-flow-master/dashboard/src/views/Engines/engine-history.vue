<template>
  <lay-row space="10">
    <lay-table :columns="columns2" :data-source="dataSource2" :size="md" skin='nob'>

      <template #options="{ data }">
        <div>
          <router-link :to="{path: '/engines/history-detail/' + data.id}">
            查看&nbsp;&nbsp;
          </router-link>
        </div>
      </template>
    </lay-table>
  </lay-row>
</template>

<script>

import {onMounted, ref} from "vue";
import {engine_history} from "../../api/module/api";

export default {

  setup() {

    let id = -1;
    let url = window.location.href;

    if (url.split("/").length > 1) {
      let all = url.split('/');
      if (all.length > 0 && !isNaN(parseFloat(all[all.length - 1]))) {
        id = all[all.length - 1];
      }
    }

    const columns2 = [
      {
        title: "引擎历史ID",
        width: "30px",
        key: "id",
      }, {
        title: "引擎名称",
        width: "120px",
        key: "engineName"
      }, {
        title: "版本",
        width: "20px",
        key: "version",
      }, {
        title: "创建时间",
        width: "80px",
        key: "created"
      }, {
        title: "最后修改时间",
        width: "80px",
        key: "updated"
      },
      {
        title: "操作",
        width: "120px",
        key: "options",
        customSlot: "options"
      }
    ]

    const dataSource2 = ref([])
    if (id === -1) {
      return {
        columns2,
        dataSource2
      }
    }

    onMounted(() => {
      const loadData = async () => {
        const {data} = await engine_history(id);
        dataSource2.value = data
      };
      loadData()
    })

    return {
      columns2,
      dataSource2
    }
  }
}
</script>