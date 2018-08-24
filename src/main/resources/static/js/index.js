$(function() {
    var dbs = [
        {name: 'cmdb'},
        {name: 'ptn'},
        {name: 'video'}
    ];
    var app = new Vue({
        el: '#main',
        data: {
            dbs: dbs
        }
    });
});
