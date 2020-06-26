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
        }
    }
});