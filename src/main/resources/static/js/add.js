var vm = new Vue({
    el: "#app",
    data() {
        return {
            imgName: ''
        }
    },
    methods: {
        imgFun: function (response, file, fileList) {
            if (response === "exists") {
                this.$message({
                    showClose: true,
                    message: '图片可能存在咯！',
                    type: 'error'
                });
            } else {
                this.$message({
                    showClose: true,
                    message: '上传失败！',
                    type: 'error'
                });
            }
        }
    }
});