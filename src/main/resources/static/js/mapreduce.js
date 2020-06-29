var vm = new Vue({
    el: "#app",
    data() {
        return {
            upImage: '',
            imgUrl: 'http://192.168.137.120/images/0.jpg',
            url: 'http://192.168.137.120/images/',
            imgList: [],
            result: '',
            rateList: [],
            nextVague: false,
            mainFun: true,
            reload: false,
            index: 0
        }
    },
    methods: {
        allFun: function () {
            if (this.upImage === '') {
                this.$message({
                    showClose: true,
                    message: '图片还没上传！',
                    type: 'error'
                });
            } else {
                axios.get('/allSearch/' + this.upImage + ".bmp")
                    .then(function (response) {
                        if (response.data === "fail") {
                            vm.$notify.error ({
                                title: '错误',
                                message: '片库找不到！'
                            });
                            vm.mainFun = false;
                            vm.reload = true;
                        } else {
                            vm.result = response.data;
                            vm.imgUrl = vm.url + response.data;
                            vm.mainFun = false;
                            vm.reload = true;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        partFun: function () {
            if (this.upImage === '') {
                this.$message({
                    showClose: true,
                    message: '图片还没上传！',
                    type: 'error'
                });
            } else {
                axios.get('/partSearch/' + this.upImage + ".bmp")
                    .then(function (response) {
                        if (response.data === "fail") {
                            vm.$notify.info ({
                                title: '错误',
                                message: '片库找不到！'
                            });
                            vm.mainFun = false;
                            vm.reload = true;
                        } else {
                            vm.result = response.data;
                            vm.imgUrl = vm.url + response.data;
                            vm.mainFun = false;
                            vm.reload = true;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        vagueFun: function () {
            if (this.upImage === '') {
                this.$message({
                    showClose: true,
                    message: '图片还没上传！',
                    type: 'error'
                });
            } else {
                axios.get('/vagueSearch/' + this.upImage + ".bmp")
                    .then(function (response) {
                        var answer = response.data;
                        var imgs = answer.split(",");
                        for (var i = 0; i < 3; i++) {
                            var an = imgs[i].split(":");
                            vm.imgList[i] = an[1];
                            vm.rateList[i] = an[0];
                        }
                        vm.result = vm.imgList[0];
                        vm.imgUrl = vm.url + vm.imgList[0];
                        vm.$notify.success ({
                            title: 'success!',
                            message: '相似度：' + vm.rateList[0] + '%'
                        });
                        vm.mainFun = false;
                        vm.nextVague = true;
                        vm.reload = true;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        nextVagueFun: function () {
            this.index = this.index + 1;
            console.log("index = " + this.index);
            vm.result = this.imgList[this.index % 3];
            vm.imgUrl = this.url + this.imgList[this.index % 3];
            vm.$notify.success ({
                title: 'success!',
                message: '相似度：' + vm.rateList[this.index % 3] + '%'
            });
        },
        reloadFun: function() {
            location.replace("http://192.168.137.120/map");
        },
        successHandler: function (response, file, fileList) {
            this.upImage = response;
        }
    }
});