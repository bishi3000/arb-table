var arbApp = angular.module('arbApp', []);

arbApp.controller('ArbTableCtrl', function ($scope, $http) {

    var currency = 'USD';

    $scope.getData = function() {
//        console.log("retreiving data for [" + currency + "]");
        $http.get('arb-table-data/' + currency).success(function(data) {
            $scope.results = data;
        });
    }

    $scope.changeCurrency = function(currencyName) {
        currency = currencyName;
        $scope.getData();
    }

    $scope.getData();

    var timer = setInterval($scope.getData, 1000);
});