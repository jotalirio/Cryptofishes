"use strict";

// --------
// WARNING:
// --------

// THIS CODE IS ONLY MADE AVAILABLE FOR DEMONSTRATION PURPOSES AND IS NOT SECURE!
// DO NOT USE IN PRODUCTION!

// FOR SECURITY REASONS, USING A JAVASCRIPT WEB APP HOSTED VIA THE CORDA NODE IS
// NOT THE RECOMMENDED WAY TO INTERFACE WITH CORDA NODES! HOWEVER, FOR THIS
// PRE-ALPHA RELEASE IT'S A USEFUL WAY TO EXPERIMENT WITH THE PLATFORM AS IT ALLOWS
// YOU TO QUICKLY BUILD A UI FOR DEMONSTRATION PURPOSES.

// GOING FORWARD WE RECOMMEND IMPLEMENTING A STANDALONE WEB SERVER THAT AUTHORISES
// VIA THE NODE'S RPC INTERFACE. IN THE COMING WEEKS WE'LL WRITE A TUTORIAL ON
// HOW BEST TO DO THIS.

const app = angular.module('demoAppModule', ['ui.bootstrap']);

// Fix for unhandled rejections bug.
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

app.controller('DemoAppController', function($scope, $http, $window, $rootScope, $location, $uibModal) {
    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/cryptofishy/";
    demoApp.cryptofishies = [];

    // This Node
    $http.get(apiBaseURL + "me").then((response) => {
        demoApp.thisNode = response.data.me;
    });

    //Function to get the CryptoFishies
    demoApp.getCryptoFishies = () => $http.get(apiBaseURL + "cryptofishies")
              .then((response) => demoApp.cryptofishies = Object.keys(response.data)
              .map((key) => response.data[key].state.data));

     //Getting the CryptoFishies
     demoApp.getCryptoFishies();

    demoApp.refresh = () => {
            demoApp.getCryptoFishies();
    };

    // Download the CryptoFishy certificate
    demoApp.showCertificateInfo = (id) => {
            window.open(apiBaseURL + "download-certificate?id=" + id, "_self");
    };
});

