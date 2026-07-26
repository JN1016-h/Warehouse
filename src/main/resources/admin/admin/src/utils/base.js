const base = {
    get() {
        // 与当前浏览器地址同域，避免 K8s/NodePort 等环境下仍写死 localhost:8080 导致下载、图片、导出走错
        const origin = (typeof window !== 'undefined' && window.location && window.location.origin)
            ? window.location.origin
            : ''
        return {
            url: origin ? `${origin}/springboot38hdw40x/` : 'http://localhost:8080/springboot38hdw40x/',
            name: "springboot38hdw40x",
            // 退出到首页链接
            indexUrl: ''
        };
    },
    getProjectName(){
        return {
            projectName: "仓库管理系统"
        } 
    }
}
export default base
