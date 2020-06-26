var vm = new Vue({
    el: "#app",
    data() {
        return {
            imgName: '',
            imgUrl: 'http://192.168.137.120/home/qinphy/Recognition/images/15.bmp',
        }
    },
    methods: {
        allFun: function () {
            if (this.imgName === '') {
                alert("图片还没上传！")
            } else {
                axios.get('/allSearch/' + this.imgName + ".bmp")
                    .then(function (response) {
                        vm.imgUrl = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        partFun: function () {
            alert("局部搜索");
        },
        vagueFun: function () {
            alert("模糊搜索");
        },
        successHandler: function (response, file, fileList) {
            this.imgName = response;
        }
    }
});