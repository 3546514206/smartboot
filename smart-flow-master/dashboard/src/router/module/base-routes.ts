import BaseLayout from '../../layouts/BaseLayout.vue';
import Login from '../../views/login/index.vue';


export default [
    {
        path: '/',
        redirect: '/engines/engines-list'
    },
    {
        component: BaseLayout,
        children: [
            {
                path: "/engines/engines-history/:id",
                component: () => import('../../views/Engines/engine-history.vue'),
                meta: {title: '历史记录', requireAuth: true},
            },
            {
                path: "/engines/engines-edit/:id",
                component: () => import('../../views/Engines/engine-edit.vue'),
                meta: {title: '查看编辑', requireAuth: true},
            },
            {
                path: "/engines/history-detail/:id",
                component: () => import('../../views/Engines/history-detail.vue'),
                meta: {title: '历史详情', requireAuth: true},
            },
            {
                path: "/report-manager/metrics-detail/:id",
                component: () => import('../../views/ReportManager/metrics_detail.vue'),
                meta: {title: '数据详情', requireAuth: true},
            },
            {
                path: "/report-manager/trace-detail/:id",
                component: () => import('../../views/ReportManager/trace_detail.vue'),
                meta: {title: '链路详情', requireAuth: true},
            },
            {
                path: "/engines/engine-edit-g6/:id",
                component: () => import('../../views/Engines/engine-edit-g6.vue'),
                meta: {title: '流程编排', requireAuth: true},
            }
        ]
    },
    {
        path: '/login',
        component: Login,
        meta: {title: '登录页面'},
    },
    {
        path: '/engines',
        component: BaseLayout,
        redirect: "/engines/engines-list",
        children:[
            {
                path: '/engines/engines-list',
                component: () => import('../../views/Engines/engine-list.vue'),
                meta: {title: '配置列表', requireAuth: true},
            },
        ]
    },
    {
        path: '/report-manager',
        component: BaseLayout,
        redirect: "/report-manager/metrics",
        children: [
            {
                path: '/report-manager/metrics',
                component: () => import('../../views/ReportManager/report-metrics.vue'),
                meta: {title: '统计数据明细', requireAuth: true},
            },
            {
                path: '/report-manager/trace',
                component: () => import('../../views/ReportManager/report-trace.vue'),
                meta: {title: '链路日志', requireAuth: true},
            },
            {
                path: '/report-manager/auxiliary',
                component: () => import('../../views/ReportManager/report-auxiliary.vue'),
                meta: {title: '链路日志', requireAuth: true},
            },
        ]

    },
    {
        path: '/engine-manager',
        component: BaseLayout,
        redirect: "/engine-manager/engines-list",
        children:[
            {
                path: '/engine-manager/engines-list',
                component: () => import('../../views/EngineManager/index.vue'),
                meta: {title: '实时视图', requireAuth: true},
            },
        ]
    },
]
