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

    // This Node: When create a new CryptoFish, the node will be the RegulatorBody
    $http.get(apiBaseURL + "me").then((response) => {
        demoApp.thisNode = response.data.me;
    });

    // This Node: When create a new CryptoFish, the node will be the RegulatorBody
    $http.get(apiBaseURL + "regulatory-body").then((response) => {
        demoApp.regulatorBody = response.data.regulatorBody;
    });

    // fishermen to issue CryptoFishies
    $http.get(apiBaseURL + "fishermen").then((response) => {
        fishermen = response.data;
    });

    demoApp.openModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'demoAppModal.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL,
                fishermen: () => fishermen
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };

    demoApp.openCreateModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'createModal.html',
            controller: 'CreateModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };

    demoApp.openTransferModal = (id) => {
        const transferModalInstance = $uibModal.open({
            templateUrl: 'transferModal.html',
            controller: 'TransferModalInstanceCtrl',
            controllerAs: 'transferModalInstance',
            resolve: {
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL,
                fishermen: () => fishermen,
                id: () => id
            }
        });

        transferModalInstance.result.then(() => {}, () => {});
    };

    //Function to get the CryptoFishies
    demoApp.getCryptoFishies = () => $http.get(apiBaseURL + "cryptofishies")
                                          .then((response) => demoApp.cryptofishies = Object.keys(response.data)
                                          .map((key) => response.data[key].state.data));

    //Getting the CryptoFishies
    demoApp.getCryptoFishies();

    //Function to get the CryptoFishies
    demoApp.getConsumedCryptoFishies = () => $http.get(apiBaseURL + "consumed-cryptofishies")
                                          .then((response) => demoApp.consumedCryptoFishies = Object.keys(response.data)
                                          .map((key) => response.data[key].state.data));

    demoApp.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    demoApp.refresh = () => {
            demoApp.getCryptoFishies();
    };


    // Download the CryptoFishy certificate
    demoApp.showCertificateInfo = (id) => {
            window.open(apiBaseURL + "download-certificate?id=" + id, "_self");
    };

});

app.controller('ModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL, fishermen) {
    const modalInstance = this;

    modalInstance.fishermen = fishermen;
    modalInstance.form = {};
    modalInstance.formError = false;

    // Validate and issue CryptoFishy.
    modalInstance.issue = () => {
        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;
            $uibModalInstance.close();

            //Owner, type and location
            const owner = modalInstance.form.fisherman;
            const type = modalInstance.form.type;
            const location = modalInstance.form.location;
            const quantity = modalInstance.form.quantity;

            // Create endpoint, call to the Rest API and handle success / fail responses.
            const createIssueEndpoint = `${apiBaseURL}issue-cryptofishy?owner=${owner}&type=${type}&location=${location}&quantity=${quantity}`;
            $http.get(createIssueEndpoint).then(
                (result) => {
                    modalInstance.displayMessage(result);
                    demoApp.getCryptoFishies();
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

    // Close issue CryptoFishy modal dialogue.
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the CryptoFishy to issue.
    function invalidFormInput() {
        return (modalInstance.form.type == "")
                || (modalInstance.form.location == "")
                || (modalInstance.form.fisherman === undefined);
    }
});

app.controller('CreateModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL) {
    const modalInstance = this;

    modalInstance.form = {};
    modalInstance.formError = false;

    // Validate and issue CryptoFishy.
    modalInstance.create = () => {
        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;
            $uibModalInstance.close();

            //Owner, type and location
            const owner = demoApp.regulatorBody;
            const type = modalInstance.form.type;
            const location = modalInstance.form.location;
            const quantity = modalInstance.form.quantity;

            // Create endpoint, call to the Rest API and handle success / fail responses.
            const createIssueEndpoint = `${apiBaseURL}issue-cryptofishy?owner=${owner}&type=${type}&location=${location}&quantity=${quantity}`;
            $http.get(createIssueEndpoint).then(
                (result) => {
                    modalInstance.displayMessage(result);
                    demoApp.getCryptoFishies();
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

    // Close issue CryptoFishy modal dialogue.
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the CryptoFishy to issue.
    function invalidFormInput() {
        return (modalInstance.form.type == "")
                || (modalInstance.form.location == "")
                || (demoApp.regulatorBody == "");
    }
});

app.controller('TransferModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, demoApp, apiBaseURL, fishermen, id) {
    const transferModalInstance = this;

    transferModalInstance.form = {};
    transferModalInstance.fishermen = fishermen;
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
            const newOwner = transferModalInstance.form.fisherman;

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
        return (transferModalInstance.form.fisherman === undefined);
    }
});

// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});