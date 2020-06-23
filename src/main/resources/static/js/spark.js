var vm = new Vue({
    el: "#app",
    data: {
        imgName: "1.bmp",
        time: "0.1s"
    },
    methods: {
        allFun: function () {
            alert("全图搜索");
        },
        partFun: function () {
            alert("局部搜索");
        },
        vagueFun: function () {
            alert("模糊搜索");
        }
    }
});