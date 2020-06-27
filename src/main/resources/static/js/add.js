var vm = new Vue({
    el: "#app",
    data() {
        return {
            imgName: ''
        }
    },
    methods: {
        imgFun: function (response, file, fileList) {
        console.log(response);
            if (response === "exists") {
                this.$message({
                    showClose: true,
                    message: '图片可能存在咯！',
                    type: 'error'
                });
            } else if (response === "fail") {
                this.$message({
                    showClose: true,
                    message: '上传失败！',
                    type: 'error'
                });
            }
        }
    }
});