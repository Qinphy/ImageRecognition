var vm = new Vue({
    el: "#app",
    data() {
        return {
            imgName: ''
        }
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