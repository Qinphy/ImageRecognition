var vm = new Vue({
    el: "#app",
    data() {
        return {
            imgUrl: 'http://192.168.137.120/images/0.jpg',
            url: 'http://192.168.137.120/images/',
            imgList: [],
            result: '',
            nextPart: false,
            nextVague: false,
            index: 0
        }
    },
    methods: {
        allFun: function () {
            if (this.imgName === '') {
                alert("图片还没上传！")
            } else {
                axios.get('/allSearch/' + this.upImage + ".bmp")
                    .then(function (response) {
                        if (response.data === "fail") {
                            vm.$notify.error ({
                                title: '错误',
                                message: '片库找不到！'
                            });
                        } else {
                            vm.result = response.data;
                            vm.imgUrl = vm.url + response.data;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        partFun: function () {
            if (this.imgName === '') {
                alert("图片还没上传！")
            } else {
                axios.get('/partSearch/' + this.upImage + ".bmp")
                    .then(function (response) {
                        if (response.data === 0 || response.data.length === 0) {
                            vm.this.$message({
                                showClose: true,
                                message: '没有找到！',
                                type: 'error'
                            });
                        } else {
                            if (response.data.length > 1) vm.nextPart = true;
                            vm.imgList = response.data;
                            vm.imgUrl = vm.url + vm.imgList[0];
                        }

                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        vagueFun: function () {
            if (this.imgName === '') {
                alert("图片还没上传！")
            } else {
                axios.get('/vagueSearch/' + this.upImage + ".bmp")
                    .then(function (response) {
                        vm.imgList = response.data;
                        vm.imgUrl = vm.url + vm.imgList[0].fileName;
                        vm.this.$message({
                            showClose: true,
                            message: '相似度：' + vm.imgList[0].rate + '%',
                            type: 'success'
                        });
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        nextVagueFun: function () {
            this.index += 1;
            var file = this.imgList[index % imgList.length];
            vm.imgUrl = this.url + file.fileName;
            vm.this.$message({
                showClose: true,
                message: '相似度：' + vm.imgList[index % imgList.length].rate + '%',
                type: 'success'
            });
        },
        nextPartFun: function () {
            this.index += 1;
            var fileName = this.imgList[index % imgList.length];
            vm.imgUrl = this.url + fileName;
        }
    }
});