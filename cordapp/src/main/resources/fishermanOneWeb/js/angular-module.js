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

app.controller('DemoAppController', function($http, $location, $uibModal) {
    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/cryptofishy/";
    let fishermen = [];
    let buyers = [];
    demoApp.cryptofishies = [];

    // This Node
    $http.get(apiBaseURL + "me").then((response) => {
        demoApp.thisNode = response.data.me;
    });

    // Fishers to trade CryptoFishies
    $http.get(apiBaseURL + "other-fishermen").then((response) => {
        fishermen = response.data;
    });

    // Buyers to transfer CryptoFishies
    $http.get(apiBaseURL + "buyers").then((response) => {
        buyers = response.data;
    });


    demoApp.openFishModal = (id) => {
        const fishModalInstance = $uibModal.open({
            templateUrl: 'fishModal.html',
            controller: 'FishModalInstanceCtrl',
            controllerAs: 'fishModalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL,
                id: () => id
            }
        });

        fishModalInstance.result.then(() => {}, () => {});
    };

    demoApp.openTransferModal = (id) => {
        const transferModalInstance = $uibModal.open({
            templateUrl: 'transferModal.html',
            controller: 'TransferModalInstanceCtrl',
            controllerAs: 'transferModalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL,
                buyers: () => buyers,
                id: () => id
            }
        });

        transferModalInstance.result.then(() => {}, () => {});
    };

    demoApp.openTradeModal = (id) => {
        const transferModalInstance = $uibModal.open({
            templateUrl: 'tradeModal.html',
            controller: 'TradeModalInstanceCtrl',
            controllerAs: 'tradeModalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL,
                fishermen: () => fishermen,
                id: () => id
            }
        });

        transferModalInstance.result.then(() => {}, () => {});
    };

    demoApp.openModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'demoAppModal.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL,
                peers: () => peers
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };

    //Function to get the CryptoFishies
    demoApp.getCryptoFishies = () => $http.get(apiBaseURL + "cryptofishies")
                                          .then((response) => demoApp.cryptofishies = Object.keys(response.data)
                                          .map((key) => response.data[key].state.data));

    //Getting the CryptoFishies
    demoApp.getCryptoFishies();

    // Download the CryptoFishy certificate
    demoApp.showCertificateInfo = (id) => {
            window.open(apiBaseURL + "download-certificate?id=" + id, "_self");
    };
});

app.controller('FishModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL, id) {
    const fishModalInstance = this;

    fishModalInstance.form = {};
    fishModalInstance.id = id;
    fishModalInstance.formError = false;

    // Validate and Fish CryptoFishy.
    fishModalInstance.fish = () => {
        if (invalidFormInput()) {
            fishModalInstance.formError = true;
        } else {
            fishModalInstance.formError = false
            $uibModalInstance.close();

            //CryptoFishy id
            const id = fishModalInstance.id;

            // Create endpoint, call to the Rest API and handle success / fail responses.
            const createFishEndpoint = `${apiBaseURL}fish-cryptofishy?id=${id}`;
            $http.get(createFishEndpoint).then(
                (result) => {
                    fishModalInstance.displayMessage(result);
                    demoApp.getCryptoFishies();
                },
                (result) => {
                    fishModalInstance.displayMessage(result);
                }
            );
        }
    };

    fishModalInstance.displayMessage = (message) => {
        const fishMsgModal = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        fishMsgModal.result.then(() => {}, () => {});
    };

    // Close Fish CryptoFishy modal dialogue.
    fishModalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the CryptoFishy to Fish.
    function invalidFormInput() {
        return (fishModalInstance.form.id == "");
    }
});

app.controller('TransferModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL, buyers, id) {
    const transferModalInstance = this;

    transferModalInstance.form = {};
    transferModalInstance.buyers = buyers;
    transferModalInstance.id = id;
    transferModalInstance.formError = false;

    // Validate and Transfer CryptoFishy.
    transferModalInstance.transfer = () => {
        if (invalidFormInput()) {
            transferModalInstance.formError = true;
        } else {
            transferModalInstance.formError = false;
            $uibModalInstance.close();

            //CryptoFishy id and newOwner
            const id = transferModalInstance.id;
            const newOwner = transferModalInstance.form.buyer;

            // Create endpoint, call to the Rest API and handle success / fail responses.
            const createTransferEndpoint = `${apiBaseURL}transfer-cryptofishy?id=${id}&newOwner=${newOwner}`;
            $http.get(createTransferEndpoint).then(
                (result) => {
                    transferModalInstance.displayMessage(result);
                    demoApp.getCryptoFishies();
                },
                (result) => {
                    transferModalInstance.displayMessage(result);
                }
            );
        }
    };

    transferModalInstance.displayMessage = (message) => {
        const transferMsgModal = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        transferMsgModal.result.then(() => {}, () => {});
    };

    // Close Transfer CryptoFishy modal dialogue.
    transferModalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the CryptoFishy to Transfer.
    function invalidFormInput() {
        return (transferModalInstance.form.buyer === undefined);
    }
});

app.controller('TradeModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL, fishermen, id) {
    const tradeModalInstance = this;

    tradeModalInstance.form = {};
    tradeModalInstance.fishermen = fishermen;
    tradeModalInstance.id = id;
    tradeModalInstance.formError = false;

    // Validate and Trade CryptoFishy.
    tradeModalInstance.trade = () => {
        if (invalidFormInput()) {
            tradeModalInstance.formError = true;
        } else {
            tradeModalInstance.formError = false;
            $uibModalInstance.close();

            //CryptoFishy id and newOwner
            const id = tradeModalInstance.id;
            const newOwner = tradeModalInstance.form.fisherman;

            // Create endpoint, call to the Rest API and handle success / fail responses.
            const createTransferEndpoint = `${apiBaseURL}transfer-cryptofishy?id=${id}&newOwner=${newOwner}`;
            $http.get(createTransferEndpoint).then(
                (result) => {
                    tradeModalInstance.displayMessage(result);
                    demoApp.getCryptoFishies();
                },
                (result) => {
                    tradeModalInstance.displayMessage(result);
                }
            );
        }
    };

    tradeModalInstance.displayMessage = (message) => {
        const tradeMsgModal = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        tradeMsgModal.result.then(() => {}, () => {});
    };

    // Close Transfer CryptoFishy modal dialogue.
    tradeModalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the CryptoFishy to Transfer.
    function invalidFormInput() {
        return (tradeModalInstance.form.fisherman === undefined);
    }
});

app.controller('ModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL, peers) {
    const modalInstance = this;

    modalInstance.peers = peers;
    modalInstance.form = {};
    modalInstance.formError = false;

    // Validate and create IOU.
    modalInstance.trade = () => {
        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;

            $uibModalInstance.close();

            const createIOUEndpoint = `${apiBaseURL}create-iou?type=${modalInstance.form.type}&location=${modalInstance.form.location}`;

            // Create PO and handle success / fail responses.
            $http.put(createIOUEndpoint).then(
                (result) => {
                    modalInstance.displayMessage(result);
                    demoApp.getIOUs();
                },
                (result) => {
                    modalInstance.displayMessage(result);
                }
            );
        }
    };

    modalInstance.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the CryptoFishy to trade.
    function invalidFormInput() {
        return (modalInstance.form.counterparty === undefined);
    }
});

// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});