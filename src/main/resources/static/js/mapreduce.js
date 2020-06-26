var vm = new Vue({
    el: "#app",
    data() {
        return {
            upImage: '',
            imgName: '',
            imgUrl: 'http://192.168.137.120/images/0.jpg',
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
                            vm.imgUrl = response.data;
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
                        console.log(response.data);
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        vagueFun: function () {
            alert("模糊搜索");
        },
        successHandler: function (response, file, fileList) {
            this.upImage = response;
            this.imgName = response;
        }
    }
});