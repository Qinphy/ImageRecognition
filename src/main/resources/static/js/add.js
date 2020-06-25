var vm = new Vue({
    el: "#app",
    data: {

    },
    methods: {
        putImgFun: function (a) {
            console.log(a.file);
            axios({
                url: "/addup",
                method: "POST",
                data: {
                    'file': a.file
                }
            }).then(function (response) {
                console.log(response);
            });
        }
    }
});