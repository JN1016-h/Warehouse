const base = {
    get() {
        return {
            url : "http://localhost:8080/springboot38hdw40x/",
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
