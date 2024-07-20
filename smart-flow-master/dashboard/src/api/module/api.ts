import Http from '../http';

export const engines = function (engineName: any, pageNo:any, pageSize:any) {
    return Http.post('/api/engines/list?engineName=' + engineName + "&pageNo=" + pageNo + "&pageSize=" + pageSize);
}

export const engine_history = function (engineId: any) {
    return Http.post('/api/engines/history?id=' + engineId);
}

export const engine_detail = function (engineId: any) {
    return Http.get('/api/engines/detail?id=' + engineId);
}

export const engine_save = function (engine: any) {
    return Http.post('/api/engines/save', engine);
}

export const engine_history_detail = function (engineId: any) {
    return Http.get('/api/engines/history_detail?historyId=' + engineId);
}

export const engine_validate = function (content: any) {
    return Http.post('/api/engines/validate', {
        content: content
    });
}

export const engine_plant_uml_view = function (content: any) {
    return Http.post('/api/engines/plant_uml_view', {
        content: content
    });
}

export const engine_online = function (engineId: any) {
    return Http.get('/api/engines/online?id=' + engineId);
}

export const engine_offline = function (engineId: any) {
    return Http.get('/api/engines/offline?id=' + engineId);
}

export const metrics_list = function (param: any) {
    return Http.post('/api/report/metrics_list', param);
}

export const metrics_detail = function (id: any) {
    return Http.get('/api/report/metrics_detail?id=' + id);
}

export const trace_list = function (param: any) {
    return Http.post('/api/report/trace_list', param);
}

export const trace_detail = function (id: any) {
    return Http.get('/api/report/trace_detail?id=' + id);
}

export const trace_detail_g6 = function (id: any) {
    return Http.get('/api/engines/g6_detail?id=' + id);
}

export const g6_detail_save = function (param: any) {
    return Http.post('/api/engines/g6_save', param);
}

export const standard_attributes = function () {
    return Http.get('/api/engines/standard_attributes');
}

export const management_engines = function () {
    return Http.get('/api/management/engines');
}

export const management_view = function (param: any) {
    return Http.post('/api/management/metrics_view', param);
}

export const report_metrics = function (param: any) {
    return Http.post('/api/report/metrics', param, {
        headers: {
            "Content-type": "application/json"
        }
    });
}

export const report_trace = function (param: any) {
    return Http.post('/api/report/trace', param, {
        headers: {
            "Content-type": "application/json"
        }
    });
}