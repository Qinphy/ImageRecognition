var vm = new Vue({
    el: "#app",
    data: {
        imgName: ''
    },
    methods: {
        allFun: function () {
            if (this.data.imgName === '') {
                alert("图片还没上传！")
            } else {
                axios.get('/allSearch/' + this.data.imgName)
                    .then(function (response) {
                        console.log(response);
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
            this.data.imgName = response;
        }
    }
});